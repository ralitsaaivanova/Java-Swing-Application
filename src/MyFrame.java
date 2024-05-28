import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PrinterException;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;


public class MyFrame extends JFrame {
    private DefaultTableModel model;
    private JTable table;
    private JButton addButton;
    private JButton deleteButton;
    private JButton updateButton;
    private JButton saveInFileButton;
    private JButton readFromFileButton;
    private JButton printButton;

    private JButton toPdfButton;

    private JFileChooser fileChooser;

    public MyFrame() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setSize(1000,700);
        this.setTitle("Application");
        this.setLocationRelativeTo(null);
        this.setLayout(null);

        String[] columnNames = {"First name","Second name","Your height"};

        model = new DefaultTableModel(columnNames, 0){
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 2 ? Double.class : String.class;
            }
        };
        //model = new DefaultTableModel(columnNames,0);

        final Object[] row = new Object[3];

        table = new JTable(model){
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component component = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    component.setBackground(row % 2 == 0 ? Color.WHITE : Color.LIGHT_GRAY);
                }
                return component;
            }
        };

        table.setVisible(true);
        table.setBounds(5,35,500,200);
        table.getTableHeader().setBounds(5,5,500,30);
        this.add(table.getTableHeader());

        table.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            {
                setHorizontalAlignment(SwingConstants.LEFT);
            }
        });
        table.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            {
                setHorizontalAlignment(SwingConstants.CENTER);
            }
        });
        table.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            {
                setHorizontalAlignment(SwingConstants.RIGHT);
            }
        });


        this.add(table);

        JPanel panelForm = new JPanel();
        panelForm.setBounds(600,5,300,270);
        panelForm.setBackground(Color.LIGHT_GRAY);

        JLabel title = new JLabel();
        title.setText("Enter information for new row adding");
        title.setForeground(Color.BLUE);
        title.setHorizontalAlignment(JLabel.CENTER);

        JLabel firstName = new JLabel();
        firstName.setText("Enter first name:");
        firstName.setHorizontalAlignment(JLabel.LEFT);

        JTextField inputFirstName = new JTextField();
        setTextFieldSize(inputFirstName);

        JLabel secondName = new JLabel();
        secondName.setText("Enter second name:");
        secondName.setHorizontalAlignment(JLabel.LEFT);

        JTextField inputSecondName = new JTextField();
        setTextFieldSize(inputSecondName);

        JLabel height = new JLabel();
        height.setText("Enter height:");
        height.setHorizontalAlignment(JLabel.LEFT);

        JTextField inputHeight = new JTextField();
        setTextFieldSize(inputHeight);


        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int i = table.getSelectedRow();
                inputFirstName.setText(model.getValueAt(i,0).toString());
                inputSecondName.setText(model.getValueAt(i,1).toString());
                inputHeight.setText(model.getValueAt(i,2).toString());
            }
        });

        panelForm.add(title);
        panelForm.add(firstName);
        panelForm.add(inputFirstName);

        panelForm.add(secondName);
        panelForm.add(inputSecondName);

        panelForm.add(height);
        panelForm.add(inputHeight);

        addButton = new JButton("Add");
        setButtonSize(addButton);
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(inputFirstName.getText().isEmpty() ||
                        inputSecondName.getText().isEmpty() ||
                        inputHeight.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null,"Please fill complete information!");
                }else {
                    row[0]=inputFirstName.getText();
                    row[1]=inputSecondName.getText();
                    double height = Double.parseDouble(inputHeight.getText());
                    BigDecimal db = new BigDecimal(height).setScale(2, RoundingMode.HALF_UP);
                    row[2]= db.doubleValue();
                    model.addRow(row);

                    inputFirstName.setText("");
                    inputSecondName.setText("");
                    inputHeight.setText("");
                    JOptionPane.showMessageDialog(null,"Saved successfully!");
                }
            }
        });


        deleteButton = new JButton("Delete");
        setButtonSize(deleteButton);
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(table.getSelectedRow()>=0){
                    int i = table.getSelectedRow();
                    model.removeRow(i);
                    JOptionPane.showMessageDialog(null,"Deleted successfully!");
                }else {
                    JOptionPane.showMessageDialog(null,"Please select a row first");
                }
            }
        });

        updateButton = new JButton("Update");
        setButtonSize(updateButton);
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int i = table.getSelectedRow();
                model.setValueAt(inputFirstName.getText(),i,0);
                model.setValueAt(inputSecondName.getText(),i,1);
                model.setValueAt(inputHeight.getText(),i,2);
            }
        });


        fileChooser = new JFileChooser("d:", FileSystemView.getFileSystemView());
        saveInFileButton = new JButton("Save in file");
        setButtonSize(saveInFileButton);
        saveInFileButton.addActionListener(e->saveToFile());

        readFromFileButton = new JButton("Read from file");
        setButtonSize(readFromFileButton);
        readFromFileButton.addActionListener(e->readFromFile());

        printButton = new JButton("Print");
        setButtonSize(printButton);
        printButton.addActionListener(e->printTable());

        toPdfButton = new JButton("Convert to PDF");
        toPdfButton.setPreferredSize(new Dimension(150,30));
        toPdfButton.addActionListener(e->exportToPDF());


        panelForm.add(addButton);
        panelForm.add(deleteButton);
        panelForm.add(updateButton);
        panelForm.add(saveInFileButton);
        panelForm.add(readFromFileButton);
        panelForm.add(printButton);
        panelForm.add(toPdfButton);
        this.add(panelForm);

    }

    private void saveToFile() {
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            fileChooser.showSaveDialog(null);
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))) {
                for (int i = 0; i < model.getRowCount(); i++) {
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        writer.write(model.getValueAt(i, j).toString() + "\t");
                    }
                    writer.newLine();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    private void readFromFile() {
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            fileChooser.showSaveDialog(null);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
                model.setRowCount(0);
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split("\t");
                    model.addRow(new Object[]{data[0], data[1], Double.parseDouble(data[2])});
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    private void exportToPDF() {
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();
                PdfPTable pdfTable = new PdfPTable(model.getColumnCount());
                for (int i = 0; i < model.getColumnCount(); i++) {
                    pdfTable.addCell(model.getColumnName(i));
                }
                for (int rows = 0; rows < model.getRowCount(); rows++) {
                    for (int cols = 0; cols < model.getColumnCount(); cols++) {
                        pdfTable.addCell(model.getValueAt(rows, cols).toString());
                    }
                }
                document.add(pdfTable);
                document.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }
    private void printTable() {
        try {
            boolean complete = table.print(JTable.PrintMode.FIT_WIDTH, new MessageFormat("Таблица"), null);
            if (complete) {
                JOptionPane.showMessageDialog(this, "Печатът е завършен успешно", "Резултат", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Печатът беше отказан", "Резултат", JOptionPane.WARNING_MESSAGE);
            }
        } catch (PrinterException pe) {
            pe.printStackTrace();
            JOptionPane.showMessageDialog(this, "Грешка при печат: " + pe.getMessage(), "Грешка", JOptionPane.ERROR_MESSAGE);
        }
    }
    public void setButtonSize(JButton button){
        button.setPreferredSize(new Dimension(120,30));
    }
    public void setTextFieldSize(JTextField field){
        field.setPreferredSize(new Dimension(170,30));
    }

}
