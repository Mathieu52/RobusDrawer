package com.innov8.robusdrawer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;

import com.fazecast.jSerialComm.SerialPort;
import com.innov8.robusdrawer.dialog.LoadingDialog;
import com.innov8.robusdrawer.draw.ErasingMode;
import com.innov8.robusdrawer.draw.PencilColor;
import com.innov8.robusdrawer.draw.robus.DrawingSettings;
import com.innov8.robusdrawer.draw.robus.RobusDrawing;
import com.innov8.robusdrawer.exception.ExceptionAlertHandler;
import com.innov8.robusdrawer.exception.SaveFailedException;
import com.innov8.robusdrawer.file.FileTransfer;
import com.innov8.robusdrawer.file.FilenameSelectorDialogue;
import com.innov8.robusdrawer.ressource.RessourceManager;
import com.innov8.robusdrawer.serial.SerialDialogue;
import com.innov8.robusdrawer.svg.SVGDocument;
import com.innov8.robusdrawer.utils.FileUIUtils;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.apache.commons.lang3.exception.ExceptionUtils;

import static com.innov8.robusdrawer.utils.FloatParser.normalizeNumberString;
import static com.innov8.robusdrawer.utils.TextFieldsUtils.forceNumberOnlyFloat;
import static com.innov8.robusdrawer.utils.TextFieldsUtils.forceNumberOnlyInt;
import static java.lang.Math.min;

import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DrawerController {
    private static final int BAUD_RATE = 115200;
    private static final Path LOG_FILE = RessourceManager.LOG_FILE;
    private static final Logger logger = Logger.getLogger(DrawerController.class.getName());

    static {
        logger.addHandler(new ConsoleHandler());
        try {
            RessourceManager.createLogFile();
            logger.addHandler(new FileHandler(LOG_FILE.toString()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    private Button blueButton;

    @FXML
    private Canvas canvas;

    @FXML
    private Button clearButton;

    @FXML
    private Button greenButton;

    //@FXML
    //private Button blackButton;

    @FXML
    private Button eraseButton;
    @FXML
    private TextField playgroundHeightField;

    @FXML
    private TextField playgroundWidthField;

    @FXML
    private TextField pointCountField;

    @FXML
    private TextField pointLimitField;

    @FXML
    private CheckBox pointLimitToggle;

    @FXML
    private VBox pointLimitMenu;

    @FXML
    private Button redButton;

    final static String SPRITE_PATH = "sprite/";

    private Stage stage;
    private Scene scene;

    private final RobusDrawing drawing = new RobusDrawing(39, 39);
    private boolean mouseOverCanvas = false;
    private final HashMap<PencilColor, String> openedMarkerSprite = new HashMap<>();
    private final HashMap<PencilColor, String> closedMarkerSprite = new HashMap<>();

    private final HashMap<Button, PencilColor> buttonAssociatedColor = new HashMap<Button, PencilColor>();

    private HashMap<Button, Runnable> updatableButtons = new HashMap<>();

    void showPointLimitMenu(boolean show) {
        pointLimitMenu.setVisible(show);
        pointLimitMenu.setDisable(!show);
        pointLimitMenu.setMinHeight(show ? Region.USE_COMPUTED_SIZE : 0);
        pointLimitMenu.setPrefHeight(show ? Region.USE_COMPUTED_SIZE : 0);
    }

    float getPlaygroundWidth() {
        return (float) drawing.getWidth();
    }

    float getPlaygroundHeight() {
        return (float) drawing.getHeight();
    }

    int getPointLimit() {
        if (pointLimitToggle.isSelected()) {
            int limit = Integer.parseInt(pointLimitField.getText());
            return min(drawing.getDrawingPoints().size(), limit);
        }

        return drawing.getDrawingPoints().size();
    }

    @FXML
    void upload() {
        try {
            Path path = Path.of(RessourceManager.TEMPORARY_DIRECTORY.toString(), "upload.txt").toAbsolutePath();
            File file = path.toFile();
            drawing.save(path.toAbsolutePath().toString(), getPointLimit());

            SerialDialogue serialDialogue = SerialDialogue.fromAvailablePorts();
            Optional<SerialPort> port = serialDialogue.showAndWait();

            if (port.isEmpty()) {
                return;
            }

            FilenameSelectorDialogue filenameSelectorDialogue = new FilenameSelectorDialogue();
            Optional<String> filename = filenameSelectorDialogue.showAndWait();

            if (filename.isEmpty()) {
                return;
            }

            port.get().setBaudRate(BAUD_RATE);
            port.get().setNumDataBits(8);
            port.get().setNumStopBits(SerialPort.ONE_STOP_BIT);
            port.get().setParity(SerialPort.NO_PARITY);

            FileTransfer fileTransfer = new FileTransfer(port.get(), file, filename.get());

            LoadingDialog loadingDialog = new LoadingDialog(getStage());
            loadingDialog.startService(fileTransfer, () -> {});

        } catch (SaveFailedException e) {
            ExceptionAlertHandler.showAlert(e);
            logger.log(Level.WARNING, "Tried to save Drawing but failed: " + ExceptionUtils.getStackTrace(e));
        }
    }

    @FXML
    void save() {
        FileUIUtils.saveFile((file) -> {
            try {
                drawing.save(file.getPath(), getPointLimit());
            } catch (SaveFailedException e) {
                ExceptionAlertHandler.showAlert(e);
                logger.log(Level.WARNING, "Tried to save Drawing but failed: " + ExceptionUtils.getStackTrace(e));
            }
        }, "Charger un dessin", new FileChooser.ExtensionFilter("Fichier texte","*.txt"), new FileChooser.ExtensionFilter("Fichier de dessins Robus","*.rbd"));
    }
    void loadFile(File file) {
        try {
            if (file.getName().endsWith(".svg")) {
                SVGDocument document = new SVGDocument(file.getPath());
                document.load(drawing, PencilColor.BLACK);
                updateCanvas();
            } else if (file.getName().endsWith(".txt") || file.getName().endsWith(".rbd")) {
                drawing.load(file.getPath());
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Tried to load drawing with file format: " + ExceptionUtils.getStackTrace(e));
        }
    }

    void loadFile(String path) {
        loadFile(new File(path));
    }

    @FXML
    void load() {
        FileUIUtils.loadFile(this::loadFile, "Sauvegarder votre dessin", new FileChooser.ExtensionFilter("Fichier de dessin","*.txt", "*.svg", "*.rbd"));
    }

    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
        initializeCanvas();
        setGlobalEventHandler(scene.getRoot());
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    void updateCanvas() {
        drawing.drawToGraphics(canvas.getGraphicsContext2D());
    }

    Image getColorSprite(PencilColor color, boolean opened) {
        String path = (opened ? openedMarkerSprite : closedMarkerSprite).get(color);
        if (path == null) {
            return null;
        }

        return new Image(this.getClass().getResourceAsStream(path));
    }

    Image getColorSprite(PencilColor color, boolean opened, double width, double height) {
        String path = (opened ? openedMarkerSprite : closedMarkerSprite).get(color);
        if (path == null) {
            return null;
        }

        return new Image(this.getClass().getResourceAsStream(path), width, height, false, false);
    }

    void updateCursor() {
        if (scene == null) {
            return;
        }

        if (!mouseOverCanvas || !getStage().isFocused()) {
            scene.setCursor(Cursor.DEFAULT);
            return;
        }

        ImageCursor imageCursor;
        if (!drawing.isErasing()) {
            Image image = getColorSprite(drawing.getPencilColor(), drawing.isToolDown(), 50, 50);
            imageCursor = new ImageCursor(image, 5, 45);
        } else {
            String eraserSprite = SPRITE_PATH + "Eraser.png";
            Image image = new Image(Objects.requireNonNull(this.getClass().getResourceAsStream(eraserSprite)), 20, 20, false, false);
            imageCursor = new ImageCursor(image, 10, 10);
        }
        scene.setCursor(imageCursor);
    }

    void onCanvasExit() {
        mouseOverCanvas = false;
        updateCursor();
    }

    void onCanvasEnter() {
        mouseOverCanvas = true;
        updateCursor();
    }
    void initializeCanvas() {
        //  Style
        canvas.getGraphicsContext2D().setLineWidth(2.0);

        //Set the cursor depending on the color
        canvas.setOnMouseEntered(event -> onCanvasEnter());

        canvas.setOnMousePressed(event -> {
            drawing.putDownTool();
        });

        canvas.setOnMouseDragEntered(event -> System.out.println("Drag entered" + System.currentTimeMillis()));

        canvas.setOnMouseDragged(event -> {
            double x = event.getX() / canvas.getWidth();
            double y = event.getY() / canvas.getHeight();

            if (mouseOverCanvas) {
                if (drawing.isErasing()) {
                    drawing.erase((float) x, (float) y, 0.02f, ErasingMode.LINE);
                } else {
                    drawing.draw((float) x, (float) y);
                }
            }
        });

        canvas.setOnMouseReleased(event -> {
            drawing.liftTool();
        });

        canvas.setOnMouseExited(event -> {
            drawing.liftTool();
            onCanvasExit();
        });

        scene.heightProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue != null) {
                canvas.setWidth(newValue.doubleValue());
                canvas.setHeight(newValue.doubleValue());
                updateCanvas();
            }
        }));
    }

    void initializeMenu() {
        showPointLimitMenu(pointLimitToggle.isSelected());
        pointLimitToggle.selectedProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue != null) {
                showPointLimitMenu(newValue);
            }
        }));
    }

    void initializeFields() {
        forceNumberOnlyFloat(playgroundWidthField);
        forceNumberOnlyFloat(playgroundHeightField);
        forceNumberOnlyInt(pointLimitField);
    }

    void updateButtons() {
        updatableButtons.forEach((button, runnable) -> runnable.run());
    }

    Optional<ImageView> getButtonImageView(ButtonBase button) {
        try {
            if (button.getChildrenUnmodifiable().size() > 0) {
                ImageView imageView = (ImageView) button.getChildrenUnmodifiable().get(0);
                return Optional.of(imageView);
            }
        } catch (NullPointerException | ClassCastException e) {
            System.err.println("Cast exception");
        }

        return Optional.empty();
    }
    void initializeButtons() {
        Button[] colorButtons = new Button[]{redButton, greenButton, blueButton};

        clearButton.setOnAction((e) -> drawing.clear());

        buttonAssociatedColor.put(redButton, PencilColor.RED);
        buttonAssociatedColor.put(greenButton, PencilColor.GREEN);
        buttonAssociatedColor.put(blueButton, PencilColor.BLUE);
        //buttonAssociatedColor.put(blackButton, PencilColor.BLACK);

        for (Button button : colorButtons) {
            button.setOnAction((e) -> {
                drawing.stopErasing();
                drawing.setPencilColor(buttonAssociatedColor.get(button));
            });
        }

        updatableButtons.forEach((button, runnable) -> {
            Optional<ImageView> imageView = getButtonImageView(button);
            imageView.ifPresent(view -> view.setEffect(new ColorAdjust(0, 0, 0, 0)));
        });

        for (Button button : colorButtons) {
            updatableButtons.put(button, () -> {
                PencilColor color = buttonAssociatedColor.get(button);

                Optional<ImageView> imageView = getButtonImageView(button);
                imageView.ifPresent(view -> {
                    Image sprite = getColorSprite(color, color == drawing.getPencilColor());
                    imageView.get().setImage(sprite);
                });
            });
        }

        eraseButton.setOnAction((e) -> drawing.startErasing());

        updateButtons();
    }

    private void initializeSprites() {
        openedMarkerSprite.put(PencilColor.RED, SPRITE_PATH + "Red_marker_opened.png");
        closedMarkerSprite.put(PencilColor.RED, SPRITE_PATH + "Red_marker_closed.png");

        openedMarkerSprite.put(PencilColor.GREEN, SPRITE_PATH + "Green_marker_opened.png");
        closedMarkerSprite.put(PencilColor.GREEN, SPRITE_PATH + "Green_marker_closed.png");

        openedMarkerSprite.put(PencilColor.BLUE, SPRITE_PATH + "Blue_marker_opened.png");
        closedMarkerSprite.put(PencilColor.BLUE, SPRITE_PATH + "Blue_marker_closed.png");

        openedMarkerSprite.put(PencilColor.BLACK, SPRITE_PATH + "Black_marker_opened.png");
        closedMarkerSprite.put(PencilColor.BLACK, SPRITE_PATH + "Black_marker_closed.png");
    }

    private void initializeDrawing() {
        StringConverter<Number> converter = new StringConverter<>() {
            @Override
            public String toString(Number object) {
                if (object == null) {
                    return "";
                }
                // Use String.format to handle cases like .0
                return normalizeNumberString(String.valueOf(object.doubleValue()));
            }

            @Override
            public Number fromString(String string) {
                if (string.isEmpty() || string.startsWith(".") || string.startsWith(",")) {
                    return 0;
                }
                return string.trim().isEmpty() ? null : Double.parseDouble(normalizeNumberString(string));
            }
        };

        Bindings.bindBidirectional(playgroundWidthField.textProperty(), drawing.widthProperty(), converter);
        Bindings.bindBidirectional(playgroundHeightField.textProperty(), drawing.heightProperty(), converter);

        pointCountField.textProperty().bind(drawing.drawingPointsProperty().sizeProperty().asString());

        drawing.pencilColorProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                updateButtons();
                updateCursor();
            }
        });

        drawing.erasingProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                updateButtons();
                updateCursor();
            }
        });

        drawing.drawingPointsProperty().addListener((observable, oldValue, newValue) -> updateCanvas());

        drawing.toolDownProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                updateCursor();
            }
        });
    }

    @FXML
    public void initialize() {
        DrawingSettings settings = new DrawingSettings();

        initializeSprites();
        initializeMenu();
        initializeFields();
        initializeButtons();
        initializeDrawing();
    }

    private void setGlobalEventHandler(Node root) {
        root.setOnMousePressed(event ->
        {
            if (event.getButton() == MouseButton.SECONDARY)
            {
                drawing.startErasing();
                eraseButton.arm();
                event.consume();
            }
        });

        root.setOnMouseReleased(event ->
        {
            if (event.getButton() == MouseButton.SECONDARY)
            {
                drawing.stopErasing();
                eraseButton.disarm();
                event.consume();
            }
        });
    }
}
