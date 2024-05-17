import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.math.BigInteger;

public class SpecializedCalculator {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            CalculatorGUI calculatorGUI = new CalculatorGUI();
            calculatorGUI.createAndShowGUI();
        });
    }
}

class CalculatorGUI {
    private JFrame frame;
    private JTextField inputField;
    private JTextField outputField;
    private JTextField signBitField;
    private int decimalPlaces = 2; // Default decimal places
    private boolean binToDecRequested = false;
    private boolean decToBinRequested = false;

    public void createAndShowGUI() {
        frame = new JFrame("Specialized Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 400); // Adjusted size
        frame.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new GridLayout(3, 1)); // 3 rows for input field, output field, and sign bit field
        inputField = new JTextField();
        outputField = new JTextField();
        signBitField = new JTextField();
        signBitField.setEditable(false);
        topPanel.add(inputField);
        topPanel.add(outputField);
        topPanel.add(signBitField);

        JPanel buttonPanel = new JPanel(new GridLayout(5, 4)); // Calculator-like layout
        addButton(buttonPanel, "7", e -> inputField.setText(inputField.getText() + "7"));
        addButton(buttonPanel, "8", e -> inputField.setText(inputField.getText() + "8"));
        addButton(buttonPanel, "9", e -> inputField.setText(inputField.getText() + "9"));
        addButton(buttonPanel, "Bin->Dec", e -> binToDecRequested = true);
        addButton(buttonPanel, "4", e -> inputField.setText(inputField.getText() + "4"));
        addButton(buttonPanel, "5", e -> inputField.setText(inputField.getText() + "5"));
        addButton(buttonPanel, "6", e -> inputField.setText(inputField.getText() + "6"));
        addButton(buttonPanel, "Dec->Bin", e -> decToBinRequested = true);
        addButton(buttonPanel, "1", e -> inputField.setText(inputField.getText() + "1"));
        addButton(buttonPanel, "2", e -> inputField.setText(inputField.getText() + "2"));
        addButton(buttonPanel, "3", e -> inputField.setText(inputField.getText() + "3"));
        addButton(buttonPanel, "ON/C", e -> clearFields());
        addButton(buttonPanel, "0", e -> inputField.setText(inputField.getText() + "0"));
        addButton(buttonPanel, ".", e -> inputField.setText(inputField.getText() + "."));
        addButton(buttonPanel, "Left", e -> moveCursorLeft());
        addButton(buttonPanel, "Right", e -> moveCursorRight());
        addButton(buttonPanel, "S", e -> displaySignBit());
        addButton(buttonPanel, "Decimal Places", e -> setDecimalPlaces());
        addButton(buttonPanel, "ENTER", e -> performConversion());

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private void addButton(JPanel panel, String label, ActionListener listener) {
        JButton button = new JButton(label);
        button.addActionListener(listener);
        panel.add(button);
    }

    private void performConversion() {
        if (binToDecRequested) {
            convertBinToDec();
            binToDecRequested = false; // Reset the flag
        } else if (decToBinRequested) {
            convertDecToBin();
            decToBinRequested = false; // Reset the flag
        }
    }

    private void convertBinToDec() {
        try {
            String binaryInput = inputField.getText();
            double result = ConversionUtil.binaryToDec(binaryInput);
            outputField.setText(String.format("%." + decimalPlaces + "f", result));
            signBitField.setText(""); // Clear sign bit field after conversion
        } catch (Exception e) {
            outputField.setText("Error");
            signBitField.setText(""); // Clear sign bit field in case of error
        }
    }

    private void convertDecToBin() {
        try {
            double decimalInput = Double.parseDouble(inputField.getText());
            String result = ConversionUtil.decToBinary(decimalInput);
            outputField.setText(result);
            signBitField.setText(""); // Clear sign bit field after conversion
        } catch (Exception e) {
            outputField.setText("Error");
            signBitField.setText(""); // Clear sign bit field in case of error
        }
    }

    private void clearFields() {
        inputField.setText("");
        outputField.setText("");
        signBitField.setText("");
    }

    private void setDecimalPlaces() {
        String input = JOptionPane.showInputDialog(frame, "Enter number of decimal places:");
        if (input != null) {
            try {
                decimalPlaces = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(frame, "Invalid input. Please enter a valid number.");
            }
        }
    }

    private void moveCursorLeft() {
        inputField.requestFocusInWindow(); // Ensure the input field has focus
        int position = inputField.getCaretPosition();
        if (position > 0) {
            inputField.setCaretPosition(position - 1);
        }
    }

    private void moveCursorRight() {
        inputField.requestFocusInWindow(); // Ensure the input field has focus
        int position = inputField.getCaretPosition();
        if (position < inputField.getText().length()) {
            inputField.setCaretPosition(position + 1);
        }
    }

    private void displaySignBit() {
        String outputText = outputField.getText();
        if (outputText.isEmpty()) {
            signBitField.setText("No result to display sign bit for.");
            return;
        }

        if (outputText.matches("[01]{64}")) {
            // Assuming the output text is binary
            signBitField.setText(ConversionUtil.getSignBit(outputText));
        } else {
            try {
                // Convert the decimal output back to binary to extract the sign bit
                double decimalValue = Double.parseDouble(outputText);
                String binaryRepresentation = ConversionUtil.decToBinary(decimalValue);
                signBitField.setText(ConversionUtil.getSignBit(binaryRepresentation));
            } catch (NumberFormatException e) {
                signBitField.setText("Invalid output.");
            }
        }
    }
}

class ConversionUtil {

    public static String decToBinary(double number) {
        long bits = Double.doubleToLongBits(number);
        StringBuilder binary = new StringBuilder(Long.toBinaryString(bits));
        while (binary.length() < 64) {
            binary.insert(0, "0");
        }
        return binary.toString();
    }

    public static double binaryToDec(String binary) {
        if (binary.length() != 64) {
            throw new IllegalArgumentException("Binary string must be 64 bits long.");
        }
        long bits = new BigInteger(binary, 2).longValue();
        return Double.longBitsToDouble(bits);
    }

    public static String getSignBit(String binary) {
        return binary.substring(0, 1);
    }
}
