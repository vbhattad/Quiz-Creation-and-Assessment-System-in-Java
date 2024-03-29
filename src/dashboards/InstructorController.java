/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dashboards;

import DAO.QuestionDAOImpl;
import DAO.ResultDAOImpl;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import Charts.AveScoreC;
import Charts.NumOfTestDuringTimeC;
import Charts.PassAndFailC;
import Charts.ScoreOverDiffLevelC;
import LoginAndSignup.SignupLoginController;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author darshanmohan
 */
public class InstructorController implements Initializable {

    @FXML
    private Button ButtonPDF;
    @FXML
    private Label lFile;
    @FXML
    private Label lLogout;
    @FXML
    private Label lrLogout;
//    @FXML
//    private ImageView imgFile;
    /*@FXML
     private Pane Diff;
     @FXML
     private Pane Num;
     @FXML
     private Pane Pass;
     @FXML
     private Pane Ave;*/

    @FXML
    private Button chart1Button;

    /**
     * Initializes the controller class.
     */
    private String chosenFile;

    /**
     *
     */
    @FXML
    public Button browse;

    /**
     *
     */
    @FXML
    public Button bStartQuiz;

    @FXML
    public ComboBox studentdropdown;

    @FXML
    public LineChart insLineChart;

    /**
     *
     */
    @FXML
    public TextField tfFile;

    /**
     *
     */
    @FXML
    public Button add;

    ArrayList<String> allStudents;

    @FXML
    private void logout() {
        lFile.setText("");
        //   imgFile.setImage(new Image(""));
        Stage stage = (Stage) browse.getScene().getWindow();
        AnchorPane page;
        try {
            page = (AnchorPane) FXMLLoader.load(getClass().getClassLoader().getResource("QuizApp/HomePage.fxml"));
            Scene scene = new Scene(page);
            stage.setScene(scene);

            stage.setResizable(false);
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(StudentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void browseFile() {
        lFile.setText("");
        //imgFile.setImage(new Image(""));
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = null;
        try {
            file = fileChooser.showOpenDialog(new Stage());
            //chosenFile = file;
        } catch (Exception e) {
            System.out.println("");
        }
        chosenFile = file.getAbsolutePath();

        tfFile.setText(file.getParent() + "/" + file.getName());

    }

    @FXML
    private void addFile() throws FileNotFoundException {

        boolean isSuccessful = false;

        try {
            QuestionDAOImpl questionsDAO = new QuestionDAOImpl();

            isSuccessful = questionsDAO.addQuestions(chosenFile);

        } catch (IOException ex) {
            Logger.getLogger(InstructorController.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (isSuccessful) {
            lFile.setText("File Added Successfully");
            // imgFile.setImage(new Image("../Media/File_Added.png"));
        } else {

            lFile.setText("Error uploading file. Check columns");
            // imgFile.setImage(new Image("../Media/File_Error.png"));

        }
    }
    AveScoreC a = new AveScoreC();
    NumOfTestDuringTimeC n = new NumOfTestDuringTimeC();
    PassAndFailC p = new PassAndFailC();
    ScoreOverDiffLevelC s = new ScoreOverDiffLevelC();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lFile.setText("");
        // imgFile.setImage(new Image(""));
        lProfessor.setText("Welcome, " + SignupLoginController.user.getFirstName());
        ResultDAOImpl dao = new ResultDAOImpl();
        allStudents = dao.getUniqueStudents();
    }

    @FXML
    private Label lProfessor;

    @FXML
    private BarChart barChart;

    @FXML
    private Button ButtonPDF1;

    @FXML
    private void showChart1(ActionEvent event) throws SQLException {
        insLineChart.setVisible(false);
        studentdropdown.setVisible(false);
        ButtonPDF1.setDisable(false);
        barChart.setVisible(true);
        XYChart.Series series1 = new XYChart.Series();
        series1 = a.getSeries();
        barChart.setAnimated(false);
        barChart.getData().clear();
        barChart.setAnimated(true);
        barChart.getData().addAll(series1);

    }

    @FXML
    private void showChart5(ActionEvent event) {
        ResultDAOImpl result = new ResultDAOImpl();
        barChart.setVisible(false);
        insLineChart.getData().clear();
        insLineChart.setVisible(true);
        insLineChart.setTitle("Scores of all the tests taken for Student: " + studentdropdown.getSelectionModel().getSelectedItem().toString());

        ArrayList<Integer> data = new ArrayList<>();
        data = result.getNoPassandFallForStudent(studentdropdown.getSelectionModel().getSelectedItem().toString());
        System.out.println(data);
        XYChart.Series series1 = new XYChart.Series();

        insLineChart.getXAxis().setLabel("Number of Tests");
        int x = 0;
        for (Integer i : data) {
            x++;
            series1.getData().add(new XYChart.Data(Integer.toString(x), i));
        }

        insLineChart.getData().addAll(series1);
        ButtonPDF1.setDisable(false);
    }

    @FXML
    private void showChart2(ActionEvent event) throws SQLException {
        insLineChart.setVisible(false);
        studentdropdown.setVisible(false);
        ButtonPDF1.setDisable(false);
        barChart.setVisible(true);
        XYChart.Series series1 = new XYChart.Series();
        series1 = n.getSeries();
        barChart.setAnimated(false);
        barChart.getData().clear();
        barChart.setAnimated(true);
        barChart.getData().addAll(series1);

    }

    @FXML
    private void showChart3(ActionEvent event) throws SQLException {
        insLineChart.setVisible(false);
        studentdropdown.setVisible(false);
        ButtonPDF1.setDisable(false);
        barChart.setVisible(true);
        List<XYChart.Series> seriesList = new ArrayList<>();
        seriesList = p.getSeries();
        barChart.setAnimated(false);
        barChart.getData().clear();
        barChart.setAnimated(true);
        barChart.getData().addAll(seriesList.get(0), seriesList.get(1));
    }

    @FXML
    private void showChart4(ActionEvent event) throws SQLException {
        insLineChart.setVisible(false);
        studentdropdown.setVisible(false);
        ButtonPDF1.setDisable(false);
        barChart.setVisible(true);
        XYChart.Series series1 = new XYChart.Series();
        List<XYChart.Series> seriesList = new ArrayList<>();
        seriesList = s.getSeries();
        barChart.setAnimated(false);
        barChart.getData().clear();
        barChart.setAnimated(true);
        barChart.getData().addAll(seriesList.get(0), seriesList.get(1));
    }
    @FXML
    private Pane paneToPDF;

    /**
     * Save the file as PDF at the desired location.
     *
     * @param event
     * @throws FileNotFoundException
     * @throws IOException
     */
    @FXML
    private void exportPDF(ActionEvent event) throws FileNotFoundException, IOException {
        Image img4 = paneToPDF.snapshot(null, null);
        ImageData imgData4;
        com.itextpdf.layout.element.Image pdfImg4;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save PDF");
        File file1 = fileChooser.showSaveDialog(paneToPDF.getScene().getWindow());
        if (file1 != null) {
            try {
                imgData4 = ImageDataFactory.create(SwingFXUtils.fromFXImage(img4, null), null);
                pdfImg4 = new com.itextpdf.layout.element.Image(imgData4);
                PdfWriter writer = new PdfWriter(new FileOutputStream(file1));
                PdfDocument pdfDoc = new PdfDocument(writer);
                Document doc = new Document(pdfDoc);
                doc.add(pdfImg4);
                doc.close();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }

    }

    @FXML
    private void setdropdown(ActionEvent e) {
        barChart.setVisible(false);
                studentdropdown.setVisible(true);
        ObservableList<String> options
                = FXCollections.observableArrayList(allStudents);
        studentdropdown.setItems(options);

    }

}
