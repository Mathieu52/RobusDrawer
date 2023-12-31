package com.innov8.robusdrawer.file;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.innov8.robusdrawer.utils.FileUtils;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.TooManyListenersException;

public class FileTransfer extends Service<Void> {
    private String remoteFileName;
    private File file;

    private SerialPort port;

    public FileTransfer(SerialPort port, File file, String remoteFileName) {
        this.port = port;
        this.file = file;
        this.remoteFileName = remoteFileName;
    }

    public FileTransfer(SerialPort port, File file) {
        this(port, file, file.getName());
    }

    @Override
    protected Task<Void> createTask() {
        FileTransferTask task = new FileTransferTask();
        port.addDataListener(task);
        task.loadFile(file);
        port.openPort();

        task.setOnSucceeded((v) -> port.closePort());
        task.setOnFailed((v) -> port.closePort());
        task.setOnCancelled((v) -> port.closePort());

        try {
            task.sendData(FileTransferTask.SOH + remoteFileName + '\n');
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return task;
    }

    private class FileTransferTask extends Task<Void> implements SerialPortDataListener {

        public static final int PACKET_SIZE = 50;
        public static final int WAIT_TIME_NANO = 50;
        public static final char ACK = 0x06;
        public static final char NAK = 0x15;
        public static final char ENQ = 0x05;
        public static final char EOT = 0x04;
        public static final char SOH = 0x01;

        private final Packet packet = new Packet("", PacketState.ACKNOWLEDGED);

        private String fileData = "";
        private int dataIndex = 0;

        @Override
        protected Void call() throws Exception {
            while (!isDone() && !isCancelled()) {
                if (dataIndex >= fileData.length() - 1) {
                    sendData(String.valueOf(EOT));
                    return null;
                }

                if (packet.getState() == PacketState.ACKNOWLEDGED) {
                    String message = fileData.substring(dataIndex, Math.min(dataIndex + PACKET_SIZE, fileData.length()));
                    dataIndex += message.length();
                    updateProgress(dataIndex, fileData.length() - 1);

                    packet.setMessage(message);
                    packet.setState(PacketState.SENT);
                    sendData(packet.getMessage());
                } else if (packet.getState() == PacketState.DAMAGED || packet.getState() == PacketState.MISSING) {
                    sendData(packet.getMessage());
                } else {
                    continue;
                }

                wait(WAIT_TIME_NANO);

                if (packet.getSize() != PACKET_SIZE) {
                    sendData(String.valueOf(ACK));
                } else {
                    sendData(String.valueOf(ENQ));
                }
            }

            return null;
        }

        @Override
        public int getListeningEvents() {
            return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
        }

        @Override
        public void serialEvent(com.fazecast.jSerialComm.SerialPortEvent event) {
            byte[] data = event.getReceivedData();
            for (byte datum : data) {
                char inputChar = (char) datum;
                switch (inputChar) {
                    case ACK -> packet.setState(PacketState.ACKNOWLEDGED);
                    case NAK -> packet.setState(PacketState.DAMAGED);
                }
            }
        }

        public void sendData(String data) throws IOException {
            port.getOutputStream().write(data.getBytes());
        }

        void loadFile(File file) {
            fileData = FileUtils.loadString(file);
            dataIndex = 0;
        }

        /**
         * Makes the task wait a set amount of nanoseconds.
         *
         * @param nanos number of nanoseconds to wait.
         */
        private void wait(int nanos) {
            try {
                Thread.sleep(nanos / 1000, nanos % 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
