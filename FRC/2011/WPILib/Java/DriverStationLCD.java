/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj;

import edu.wpi.first.wpilibj.communication.FRCControl;
import edu.wpi.first.wpilibj.parsing.IInputOutput;

/**
 * Provide access to LCD on the Driver Station.
 *
 * Buffer the printed data locally and then send it
 * when UpdateLCD is called.
 */
public class DriverStationLCD extends SensorBase implements IInputOutput{

    private static DriverStationLCD m_instance;
    /**
     * Driver station timeout in milliseconds
     */
    public static final int kSyncTimeout_ms = 20;
    /**
     * Command to display text
     */
    public static final int kFullDisplayTextCommand = 0x9FFF;
    /**
     * Maximum line length for Driver Station display
     */
    public static final int kLineLength = 21;

    /**
     * The line number on the Driver Station LCD
     */
    public static class Line {

        /**
         * The integer value representing this enumeration
         */
        public final int value;
        static final int kMain6_val = 0;
        static final int kUser2_val = 1;
        static final int kUser3_val = 2;
        static final int kUser4_val = 3;
        static final int kUser5_val = 4;
        static final int kUser6_val = 5;
        /**
         * Line at the Bottom of the main screen
         */
        public static final Line kMain6 = new Line(kMain6_val);
        /**
         * Line on the user screen
         */
        public static final Line kUser2 = new Line(kUser2_val);
        /**
         * Line on the user screen
         */
        public static final Line kUser3 = new Line(kUser3_val);
        /**
         * Line on the user screen
         */
        public static final Line kUser4 = new Line(kUser4_val);
        /**
         * Line on the user screen
         */
        public static final Line kUser5 = new Line(kUser5_val);
        /**
         * Line on the user screen
         */
        public static final Line kUser6 = new Line(kUser6_val);

        private Line(int value) {
            this.value = value;
        }
    }

    byte[] m_textBuffer;

    /**
     * Get an instance of the DriverStationLCD
     * @return an instance of the DriverStationLCD
     */
    public static synchronized DriverStationLCD getInstance() {
        if (m_instance == null)
            m_instance = new DriverStationLCD();
        return m_instance;
    }

    /**
     * DriverStationLCD contructor.
     *
     * This is only called once the first time GetInstance() is called
     */
    private DriverStationLCD() {
        m_textBuffer = new byte[FRCControl.USER_DS_LCD_DATA_SIZE];

        for (int i = 0; i < FRCControl.USER_DS_LCD_DATA_SIZE; i++) {
            m_textBuffer[i] = ' ';
        }

        m_textBuffer[0] = (byte) (kFullDisplayTextCommand >> 8);
        m_textBuffer[1] = (byte) kFullDisplayTextCommand;
    }

    /**
     * Send the text data to the Driver Station.
     */
    public synchronized void updateLCD() {
        FRCControl.setUserDsLcdData(m_textBuffer, FRCControl.USER_DS_LCD_DATA_SIZE, kSyncTimeout_ms);
    }

    /**
     * Print formatted text to the Driver Station LCD text buffer.
     *
     * Use UpdateLCD() periodically to actually send the test to the Driver Station.
     *
     * @param line The line on the LCD to print to.
     * @param startingColumn The column to start printing to.  This is a 1-based number.
     * @param text the text to print
     */
    public void println(Line line, int startingColumn, String text) {
        int start = startingColumn - 1;
        int maxLength = kLineLength - start;

        if (startingColumn < 1 || startingColumn > kLineLength) {
            throw new IndexOutOfBoundsException("Column must be between 1 and " + kLineLength + ", inclusive");
        }

        int length = text.length();
        int finalLength = (length < maxLength ? length : maxLength);
        synchronized (this) {
            for (int i = 0; i < finalLength; i++) {
                m_textBuffer[i + start + line.value * kLineLength + 2] = (byte)text.charAt(i);
            }
        }
    }

    /**
     * Print formatted text to the Driver Station LCD text buffer.
     *
     * Use UpdateLCD() periodically to actually send the test to the Driver Station.
     *
     * @param line The line on the LCD to print to.
     * @param startingColumn The column to start printing to.  This is a 1-based number.
     * @param text the text to print
     */
    public void println(Line line, int startingColumn, StringBuffer text) {
        int start = startingColumn - 1;
        int maxLength = kLineLength - start;

        if (startingColumn < 1 || startingColumn > kLineLength) {
            throw new IndexOutOfBoundsException("Column must be between 1 and " + kLineLength + ", inclusive");
        }

        int length = text.length();
        int finalLength = (length < maxLength ? length : maxLength);
        synchronized (this) {
            for (int i = 0; i < finalLength; i++) {
                m_textBuffer[i + start + line.value * kLineLength + 2] = (byte) text.charAt(i);
            }
        }
    }
}
