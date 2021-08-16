package sample;

import com.pdftron.pdf.OCRModule;
import com.pdftron.pdf.PDFDoc;
import com.pdftron.pdf.PDFNet;
import com.pdftron.sdf.SDFDoc;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.net.MalformedURLException;
import java.util.Random;


public class Controller {
    @FXML
    private Pane pane;
    private String path;
    private boolean fileLoaded;
    private PDFDoc document;
    private boolean fileScanned;
    private ImageView img;

    public void Load() {
        if(this.img != null) {
            pane.getChildren().remove(img);
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load your file");
        FileChooser.ExtensionFilter filterPDF = new FileChooser.ExtensionFilter("Image files","*.png", "*.jpg", "*.jpeg", "*.JPG", "*.PNG", "*.JPEG");
        fileChooser.getExtensionFilters().addAll(filterPDF);
        Stage mainStage = new Stage();
        File selectedFile = fileChooser.showOpenDialog(mainStage);
        if(selectedFile != null) {
            this.path = selectedFile.getAbsolutePath();
            this.fileLoaded = true;
            try {
                img = new ImageView(selectedFile.toURI().toURL().toExternalForm());
                double x = img.getImage().getWidth();
                double y = img.getImage().getHeight();
                double scaleFactor = x > y ? 360.0 / x : 360.0 / y;
                if(scaleFactor < 1) {
                    img.setFitWidth(x * scaleFactor);
                    img.setFitHeight(y * scaleFactor);
                }
                pane.getChildren().add(img);
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Your file has been loaded successfully.");
                alert.showAndWait();
            } catch (MalformedURLException e) {
//                System.out.print(e.getMessage());
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Loading your image failed, try again.");
                alert.showAndWait();
            }
        }
        else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information dialog");
            alert.setHeaderText(null);
            alert.setContentText("You didn't select any file or there was an error when loading it.");
            alert.showAndWait();
        }
    }

    public void Scan() {
        if(this.fileLoaded) {
            try {
                PDFNet.initialize();
                PDFNet.addResourceSearchPath("./Lib");
                this.document = new PDFDoc();
                OCRModule.imageToPDF(document, this.path, null);
                this.fileScanned = true;
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Your file has been scanned successfully.");
                alert.showAndWait();
            }
            catch (Exception e) {
//                System.out.println(e.getMessage());
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Unexpected failure when scanning your document");
                alert.showAndWait();
            }
        }
        else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information dialog");
            alert.setHeaderText(null);
            alert.setContentText("You didn't load a file. Press the Load button to proceed.");
            alert.showAndWait();
        }
    }

    public void Save() {
        if(this.fileScanned) {
            try {
                DirectoryChooser chooser = new DirectoryChooser();
                chooser.setTitle("Where do you want to save the file?");
                File dir = chooser.showDialog(new Stage());
                String dirAbsolutePath = dir.getAbsolutePath();
                System.out.println(dirAbsolutePath);
                Random random = new Random();
                int nr = random.nextInt();
                this.document.save((dir + "/EasyScanner" + nr + ".pdf"), SDFDoc.SaveMode.LINEARIZED, null);
                this.document.close();
                this.document = null;
                this.fileScanned = false;
                this.path = null;
                pane.getChildren().remove(this.img);
                this.img = null;
                PDFNet.terminate();
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Your file has been saved successfully.");
                alert.showAndWait();
            }
            catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Something wrong happened, try again to scan the file.");
                alert.showAndWait();
            }
        }
        else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information dialog");
            alert.setHeaderText(null);
            alert.setContentText("The loaded file was not scanned yet. Press the scan button to proceed.");
            alert.showAndWait();
        }
    }
}