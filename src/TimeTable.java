import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class TimeTable extends JFrame implements ActionListener {
    private JPanel screen = new JPanel();
    private JPanel tools = new JPanel();
    private JButton[] tool;
    private JTextField[] field;
    private CourseArray courses;
    private Color[] CRScolor;
    private int currentStep = 1;
    private Autoassociator network;
    private static final Logger LOGGER = Logger.getLogger(TimeTable.class.getName());
    int trainingCount;

    public TimeTable() {
        super("Dynamic Time Table");
        this.CRScolor = new Color[]{Color.RED, Color.GREEN, Color.BLACK};
        this.setSize(500, 800);
        this.setLayout(new FlowLayout());
        this.screen.setPreferredSize(new Dimension(400, 800));
        this.add(this.screen);
        this.setTools();
        this.add(this.tools);
        this.setVisible(true);
        try {
            FileHandler fileHandler = new FileHandler("app.log");
            LOGGER.addHandler(fileHandler);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
        }
    }

    public void setTools() {
        String[] capField = new String[]{"Slots:", "Courses:", "Clash File:", "Iters:", "Shift:"};
        this.field = new JTextField[capField.length];
        String[] capButton = new String[]{"Load", "Start", "Step", "Print", "Exit", "Continue", "Train", "Update"};
        this.tool = new JButton[capButton.length];
        this.tools.setLayout(new GridLayout(2 * capField.length + capButton.length, 1));

        int i;
        for (i = 0; i < this.field.length; ++i) {
            this.tools.add(new JLabel(capField[i]));
            this.field[i] = new JTextField(5);
            this.tools.add(this.field[i]);
        }

        for (i = 0; i < this.tool.length; ++i) {
            this.tool[i] = new JButton(capButton[i]);
            this.tool[i].addActionListener(this);
            this.tools.add(this.tool[i]);
        }

        field[0].setText("17");
        field[1].setText("682");
        field[2].setText("Files/car-s-91.stu");
        field[3].setText("1");

        tool[7].setEnabled(false);
        tool[6].setEnabled(false);
    }

    public void draw() {
        Graphics g = this.screen.getGraphics();
        int width = Integer.parseInt(this.field[0].getText()) * 10;

        for (int courseIndex = 1; courseIndex < this.courses.length(); ++courseIndex) {
            g.setColor(this.CRScolor[this.courses.status(courseIndex) > 0 ? 0 : 1]);
            g.drawLine(0, courseIndex, width, courseIndex);
            g.setColor(this.CRScolor[this.CRScolor.length - 1]);
            g.drawLine(10 * this.courses.slot(courseIndex), courseIndex, 10 * this.courses.slot(courseIndex) + 10, courseIndex);
        }
    }

    private int getButtonIndex(JButton source) {
        int result;
        for (result = 0; source != this.tool[result]; ++result) {
        }
        return result;
    }

    public void actionPerformed(ActionEvent click) {
        int iteration;
        switch (this.getButtonIndex((JButton) click.getSource())) {
            case 0:
                int slots = Integer.parseInt(this.field[0].getText());
                this.courses = new CourseArray(Integer.parseInt(this.field[1].getText()) + 1, slots);
                this.courses.readClashes(this.field[2].getText());
                this.draw();

                network = new Autoassociator(courses);
                trainingCount = (int) Math.ceil(0.139 * this.courses.length());
                tool[6].setEnabled(true);
                tool[7].setEnabled(false);
                LOGGER.info("Slots: " + this.field[0].getText() + "\n" + "Shifts: " + this.field[4].getText());
                break;
            case 1:
                int min = 2147483647;
                int step = 0;

                for (iteration = 1; iteration < this.courses.length(); ++iteration) {
                    this.courses.setSlot(iteration, 0);
                }

                for (iteration = 1; iteration <= Integer.parseInt(this.field[3].getText()); ++iteration) {
                    this.courses.iterate(Integer.parseInt(this.field[4].getText()));
                    this.draw();
                    int clashes = this.courses.clashesLeft();
                    if (clashes < min) {
                        min = clashes;
                        step = iteration;
                    }
                }
                LOGGER.info("Operation: Start" + "\n" + "Shift:" + this.field[4].getText() + "\tMin clashes:" + min + "\tat step " + step);
                System.out.println("Shift = " + this.field[4].getText() + "\tMin clashes = " + min + "\tat step " + step);
                this.setVisible(true);
                break;
            case 2: //step
                this.courses.iterate(Integer.parseInt(this.field[4].getText()));
                this.draw();
                LOGGER.info("Operation: Step" + "\n" + "Shift:" + this.field[4].getText() + "\tMin clashes:" + this.courses.clashesLeft());
                break;
            case 3:
                System.out.println("Exam\tSlot\tClashes");

                StringBuilder print = new StringBuilder("\"Exam\\tSlot\\tClashes\"");
                for (iteration = 1; iteration < this.courses.length(); ++iteration) {
                    print.append("\n").append(iteration).append("\t").append(this.courses.slot(iteration)).append("\t").append(this.courses.status(iteration));
                }

                LOGGER.info(String.valueOf(print));
                return;
            case 4:
                System.exit(0);
            case 5:
                int min1 = Integer.MAX_VALUE;
                int step1 = 0;

                // Initialize min and step with current state
                for (int iteration1 = 1; iteration1 <= Integer.parseInt(this.field[3].getText()); ++iteration1) {
                    this.courses.iterate(Integer.parseInt(this.field[4].getText()));
                    this.draw();
                    int clashes = this.courses.clashesLeft();
                    if (clashes < min1) {
                        min1 = clashes;
                        step1 = iteration1;
                    }
                }

                System.out.println("Shift = " + this.field[4].getText() + "\tMin clashes = " + min1 + "\tat step " + step1);
                this.setVisible(true);
                currentStep = step1 + 1; // Update the current step for next continuation
                LOGGER.info("Operation: Continue" + "\n" + "Shift:" + this.field[4].getText() + "\tMin clashes:" + this.courses.clashesLeft());
                break;
            case 6://train
                List<int[]> clashFreeTimeSlots = courses.getClashFreeTimeSlots(1);

                if(clashFreeTimeSlots.isEmpty()) {
                    tool[6].setEnabled(false);
                    tool[7].setEnabled(true);
                }

                for (int i = 0; i < clashFreeTimeSlots.size(); i++) {
                    if (--trainingCount > 0) {
                        network.training(clashFreeTimeSlots.get(i));
                    } else {
                        tool[6].setEnabled(false);
                        tool[7].setEnabled(true);
                    }
                }
                break;
            case 7: //update
                List<int[]> clashedTimeSlots = courses.getClashedTimeSlots();
                if (clashedTimeSlots.size() > 0) {
                    int randomSlot = new Random().nextInt(clashedTimeSlots.size());
                    int[] timeslotUpdated = clashedTimeSlots.get(randomSlot);
                    int[] result = network.unitUpdate(timeslotUpdated);
                    int courseIndex = result[0];
                    (courses.getElements())[courseIndex].mySlot = randomSlot;
                    this.draw();
                    LOGGER.info("Operation: Update network" + "\n" + "Shift:" + this.field[4].getText() + "\tMin clashes:" + this.courses.clashesLeft());
                }
        }
    }

    public static void main(String[] args) {
        new TimeTable();
    }
}