/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package StudentQuizTest;

import Model.Question;
import Model.AnswerOption;
import Model.Result;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author katha
 */
public class QuizTest extends Application {

    String difficultyLevelOptions[] = {"Easy", "Medium", "Hard", "Mix"}; // options for dropdown
    int totalQuestionOptions[] = {10, 15, 20}; // options for dropdown
    String difficultyLevel;
    ArrayList<Question> allQuestions;
    Result quizResult = new Result();
    int counter = 0;

    public void setAllQuestions(int totalQuestions, String diffLevel) {
        difficultyLevel = diffLevel;
        DAO.QuestionDAOImpl dao = new DAO.QuestionDAOImpl();
        //allQuestions = dao.getQuestions(totalQuestions, difficultyLevel);
        allQuestions = getquestions();
    }

    ArrayList<Question> getquestions() {
        ArrayList<AnswerOption> optionlist = new ArrayList<>();
        ArrayList<Question> questionlist = new ArrayList<>();
        optionlist.add(new AnswerOption("option1", false));
        optionlist.add(new AnswerOption("option2", true));
        optionlist.add(new AnswerOption("option3", false));
        optionlist.add(new AnswerOption("option4", false));
        questionlist.add(new Question("MC", "H", "Que1", optionlist));
        optionlist = new ArrayList<>();
        optionlist.add(new AnswerOption("option1", true));
        optionlist.add(new AnswerOption("option2", true));
        optionlist.add(new AnswerOption("option3", false));
        questionlist.add(new Question("MA", "H", "Que2", optionlist));
        optionlist = new ArrayList<>();
        optionlist.add(new AnswerOption("option", true));
        questionlist.add(new Question("FIB", "H", "Que3", optionlist));
        optionlist = new ArrayList<>();
        optionlist.add(new AnswerOption("option1", true));
        optionlist.add(new AnswerOption("option2", false));
        questionlist.add(new Question("TF", "H", "Que4", optionlist));
        return questionlist;
    }

    HashMap<Integer, ArrayList<CheckBox>> mapCheckBoxes = new HashMap<>();
    HashMap<Integer, ArrayList<RadioButton>> mapRadioButtons = new HashMap<>();
    HashMap<Integer, String> mapFIB = new HashMap<>();

    static int questionNumber = 0;

    static Label question = new Label();
    static Label options = new Label();

    static HBox hbox = new HBox(50);
    static VBox vbox;
    static HBox hbButtons;

    static Button bNext = new Button("Next");
    static Button bPrevious = new Button("Previous");

    static Scene scene;

    @Override
    public void start(Stage stage) throws Exception {
        allQuestions = getquestions();
        question.setPadding(new Insets(50, 50, 50, 50));
        options.setPadding(new Insets(50, 50, 50, 50));
        hbButtons = new HBox(75);
        bPrevious.setMinWidth(hbButtons.getPrefWidth());
        bPrevious.setOnAction(e -> {
            questionNumber--;
            displayQuestion(allQuestions.get(questionNumber));
        });
        bNext.setMinWidth(hbButtons.getPrefWidth());
        bNext.setOnAction(e -> {
            questionNumber++;
            displayQuestion(allQuestions.get(questionNumber));
        });

        hbButtons.setAlignment(Pos.CENTER);
        hbButtons.getChildren().addAll(bPrevious, bNext);

        displayQuestion(allQuestions.get(questionNumber));

        vbox = new VBox(25);

        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.setPadding(new Insets(50, 50, 50, 50));

        vbox.getChildren().addAll(question, options);
        vbox.getChildren().add(hbox);
        vbox.getChildren().add(hbButtons);

        scene = new Scene(vbox, 1280, 768);

        stage.setScene(scene);

        stage.show();

    }

    void displayQuestion(Question que) {
        enableDisableButton();
        hbox.getChildren().clear();
        hbox.setAlignment(Pos.CENTER);
        question.setText(que.getQuestionDesc());
        char ch = 'a';
        String strOption = "";
        RadioButton radio;
        ArrayList<CheckBox> MACheckBoxes;
        ArrayList<RadioButton> RadioButtons;
        ToggleGroup togglegroup;

        switch (que.getQuestionType()) {

            case "MC":
                if (que.getIsAnswered()) {
                    for (AnswerOption option : que.getOptionList()) {
                        strOption += ch + ". " + option.getOptionDesc() + "\n";
                        ch += 1;
                    }
                    RadioButtons = mapRadioButtons.get(questionNumber);
                    RadioButtons.stream().forEach(rb -> hbox.getChildren().add(rb));
                } else {
                    RadioButtons = new ArrayList<>();
                    togglegroup = new ToggleGroup();
                    for (AnswerOption option : que.getOptionList()) {
                        strOption += ch + ". " + option.getOptionDesc() + "\n";
                        radio = new RadioButton(Character.toString(ch));
                        radio.setToggleGroup(togglegroup);
                        radio.setUserData(option.getOptionDesc());
                        radio.selectedProperty().addListener((obs, oldval, newval) -> {
                            if (newval) {
                                computeRadiobtnAns();
                            }
                        });
                        RadioButtons.add(radio);
                        hbox.getChildren().add(radio);
                        ch += 1;
                    }
                    mapRadioButtons.put(questionNumber, RadioButtons);
                }
                options.setText(strOption);
                break;

            case "MA":
                if (que.getIsAnswered()) {
                    for (AnswerOption option : que.getOptionList()) {
                        strOption += ch + ". " + option.getOptionDesc() + "\n";
                        ch += 1;
                    }
                    MACheckBoxes = mapCheckBoxes.get(questionNumber);
                    MACheckBoxes.stream().forEach(cb -> hbox.getChildren().add(cb));
                } else {
                    CheckBox check;
                    MACheckBoxes = new ArrayList<>();
                    for (AnswerOption option : que.getOptionList()) {
                        strOption += ch + ". " + option.getOptionDesc() + "\n";
                        check = new CheckBox(Character.toString(ch));
                        check.setAllowIndeterminate(false);
                        check.setSelected(false);
                        check.setUserData(option.getOptionDesc());
                        check.selectedProperty().addListener((obs, oldval, newval) -> {
                            computeCheckBoxAns();
                        });
                        MACheckBoxes.add(check);
                        hbox.getChildren().add(check);
                        ch += 1;
                    }
                    mapCheckBoxes.put(questionNumber, MACheckBoxes);
                }
                options.setText(strOption);
                break;

            case "TF":
                if (que.getIsAnswered()) {
                    for (AnswerOption option : que.getOptionList()) {
                        strOption += ch + ". " + option.getOptionDesc() + "\n";
                        ch += 1;
                    }
                    RadioButtons = mapRadioButtons.get(questionNumber);
                    RadioButtons.stream().forEach(rb -> hbox.getChildren().add(rb));
                } else {
                    RadioButtons = new ArrayList<>();
                    togglegroup = new ToggleGroup();
                    for (AnswerOption option : que.getOptionList()) {
                        radio = new RadioButton(option.getOptionDesc());
                        radio.setToggleGroup(togglegroup);
                        radio.setUserData(option.getOptionDesc());
                        radio.selectedProperty().addListener((obs, oldval, newval) -> {
                            if (newval) {
                                computeRadiobtnAns();
                            }
                        });
                        RadioButtons.add(radio);
                        hbox.getChildren().add(radio);
                    }
                    mapRadioButtons.put(questionNumber, RadioButtons);
                }
                break;

            case "FIB":

                options.setText("");

                Label lblAnswer = new Label("Answer:");
                lblAnswer.setPadding(new Insets(0, 15, 40, 0));

                TextField tfAnswer = new TextField();
                tfAnswer.setPadding(new Insets(15, 15, 40, 0));

                tfAnswer.setMinHeight(50);
                tfAnswer.setMaxHeight(100);
                tfAnswer.alignmentProperty();

                if (que.getIsAnswered()) {
                    tfAnswer.setText(mapFIB.get(questionNumber));
                }

                tfAnswer.textProperty().addListener((observable, oldValue, newValue) -> {
                    computeTextAns(newValue);
                });
                hbox.getChildren().addAll(lblAnswer, tfAnswer);
                break;

            default:
                question.setText("Error.");
                break;

        }
    }

    void computeTextAns(String inputAnswer) {
        mapFIB.put(questionNumber, inputAnswer);
        Question que = allQuestions.get(questionNumber);
        que.setIsAnswered(true);
        String correctAns = que.getOptionList().get(0).getOptionDesc();
        if (correctAns.equals(inputAnswer)) {
            allQuestions.get(questionNumber).setIscorrect(true);
        } else {
            allQuestions.get(questionNumber).setIscorrect(false);
        }
    }

    void computeCheckBoxAns() {
        Question que = allQuestions.get(questionNumber);
        que.setIsAnswered(true);
        int totalcorrect = 0;
        List<AnswerOption> correctOptions = que.getOptionList().stream().filter(option -> option.getIsCorrect() == true).collect(Collectors.toList());
        for (CheckBox cb : mapCheckBoxes.get(questionNumber)) {
            if (cb.isSelected()) {
                boolean isOptionmatch = correctOptions.stream().anyMatch(option -> option.getOptionDesc() == cb.getUserData());
                if (isOptionmatch) {
                    totalcorrect++;
                } else {
                    totalcorrect--;
                }
            }
        }
        if (correctOptions.size() == totalcorrect) {
            // All correct checkboxes are ticked
            allQuestions.get(questionNumber).setIscorrect(true);
        } else {
            allQuestions.get(questionNumber).setIscorrect(false);
        }
    }

    void computeRadiobtnAns() {
        Question que = allQuestions.get(questionNumber);
        que.setIsAnswered(true);
        String correctAns = que.getOptionList().stream().filter(option -> option.getIsCorrect() == true).findFirst().get().getOptionDesc();
        mapRadioButtons.get(questionNumber).stream().filter((rb) -> (rb.isSelected())).forEach((rb) -> {
            if (correctAns.equals(rb.getUserData())) {
                allQuestions.get(questionNumber).setIscorrect(true);
                System.out.println("Radio correct");
            } else {
                allQuestions.get(questionNumber).setIscorrect(false);
                System.out.println("Radio InCorrect");
            }
        });

    }

    void enableDisableButton() {

        if (questionNumber <= 0) {
            bPrevious.setDisable(true);
            questionNumber = 0;
        } else if (questionNumber >= allQuestions.size() - 1) {
            bNext.setDisable(true);
            questionNumber = allQuestions.size() - 1;
        } else {
            bPrevious.setDisable(false);
            bNext.setDisable(false);
        }
    }

    void endQuiz() {
        int totalQuestions = allQuestions.size();
        int totalCorrect = 0;
        quizResult.setDifficultyLevel(difficultyLevel);
        switch (difficultyLevel) {
            case "Easy":
                totalCorrect = (int) allQuestions.stream().filter(que -> que.getIscorrect()).count();
                quizResult.setNoOfCorrectEasy(totalCorrect);
                quizResult.setTotalNoOfEasy(totalQuestions);
                break;
            case "Medium":
                totalCorrect = (int) allQuestions.stream().filter(que -> que.getIscorrect()).count();
                quizResult.setNoOfCorrectMedium(totalCorrect);
                quizResult.setTotalNoOfMedium(totalQuestions);
                break;
            case "Hard":
                totalCorrect = (int) allQuestions.stream().filter(que -> que.getIscorrect()).count();
                quizResult.setNoOfCorrectHard(totalCorrect);
                quizResult.setTotalNoOfHard(totalQuestions);
                break;
            case "Mix":
                totalCorrect = (int) allQuestions.stream().filter(que -> que.getIscorrect() && que.getDifficulty() == "E").count();
                totalQuestions = (int) allQuestions.stream().filter(que -> que.getDifficulty() == "E").count();
                quizResult.setNoOfCorrectEasy(totalCorrect);
                quizResult.setTotalNoOfEasy(totalQuestions);
                totalCorrect = (int) allQuestions.stream().filter(que -> que.getIscorrect() && que.getDifficulty() == "M").count();
                totalQuestions = (int) allQuestions.stream().filter(que -> que.getDifficulty() == "E").count();
                quizResult.setNoOfCorrectMedium(totalCorrect);
                quizResult.setTotalNoOfMedium(totalQuestions);
                totalCorrect = (int) allQuestions.stream().filter(que -> que.getIscorrect() && que.getDifficulty() == "H").count();
                totalQuestions = (int) allQuestions.stream().filter(que -> que.getDifficulty() == "E").count();
                quizResult.setNoOfCorrectHard(totalCorrect);
                quizResult.setTotalNoOfHard(totalQuestions);
                break;
            default:
                break;
        }
        calculateGrade();

        /* Things to Implement
        1. Insert code to save the quiz result in database
        2. move to student dashboard
        3. show graph for quiz performance
         */

      String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
        String insertResult = "INSERT INTO TABLE Quizapp.Result VALUES ('" + quizResult.getAndrewId() + "','" +
                                                                             quizResult.getNoOfCorrectEasy() + "','" +
                                                                             quizResult.getNoOfCorrectMedium() + "','" +
                                                                             quizResult.getTotalNoOfHard() + "','" +
                                                                             quizResult.getTotalNoOfEasy() + "','" +
                                                                             quizResult.getTotalNoOfMedium() + "','" +
                                                                             quizResult.getTotalNoOfHard() + "','" +
                                                                             timeStamp + "'," +
                                                                             quizResult.getGrade() + ",'" +
                                                                             quizResult.getDifficultyLevel() + "','" +
                                                                             quizResult.getScore() + ")" ;
        
       
//        try {
//            Connection connect = DriverManager.getConnection(url); //Create connection
//            Statement statement = connect.createStatement(); //Connect to DB
//            statement.executeQuery(insertResult);
//         }catch (SQLException e){
//             System.out.println("SQL Exception: " +e);
//         }
                                                                             
    }

    void calculateGrade() {
        int totalCorrectAns = quizResult.getTotalNoOfEasy() + quizResult.getTotalNoOfMedium() + quizResult.getTotalNoOfHard();
        quizResult.setScore(totalCorrectAns);
        int totalQues = allQuestions.size();
        double percentage = totalCorrectAns / (double) totalQues;
        if (percentage < 0.5) {
            quizResult.setGrade(0);
        } else {
            quizResult.setGrade(1);
        }
    }

}
