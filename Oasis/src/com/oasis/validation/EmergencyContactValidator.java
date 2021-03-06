package com.oasis.validation;

import com.oasis.factory.NotificationFactory;
import com.oasis.ui.component.Notification;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.TextField;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmergencyContactValidator implements Validator {
    private final TextField nameTextField;
    private final TextField relationshipTextField;
    private final TextField telephoneTextField;
    private final TextField addressTextField;

    private StringProperty nameValue = new SimpleStringProperty();
    private StringProperty relationshipValue = new SimpleStringProperty();
    private StringProperty telephoneValue = new SimpleStringProperty();
    private StringProperty addressValue = new SimpleStringProperty();

    private ChangeListener<String> nameChangeListener;
    private ChangeListener<String> relationshipChangeListener;
    private ChangeListener<String> telephoneChangeListener;
    private ChangeListener<String> addressChangeListener;

    private boolean nameValid = false;
    private boolean relationshipValid = false;
    private boolean telephoneValid = false;
    private boolean addressValid = false;

    public EmergencyContactValidator(TextField nameTextField, TextField relationshipTextField, TextField telephoneTextField,
                                     TextField addressTextField) {
        this.nameTextField = nameTextField;
        this.relationshipTextField = relationshipTextField;
        this.telephoneTextField = telephoneTextField;
        this.addressTextField = addressTextField;

        nameValue.bind(nameTextField.textProperty());
        relationshipValue.bind(relationshipTextField.textProperty());
        telephoneValue.bind(telephoneTextField.textProperty());
        addressValue.bind(addressTextField.textProperty());

        Platform.runLater(() -> {
            nameTextField.getStyleClass().remove("text-field-invalid");
            relationshipTextField.getStyleClass().remove("text-field-invalid");
            telephoneTextField.getStyleClass().remove("text-field-invalid");
            addressTextField.getStyleClass().remove("text-field-invalid");
        });

        nameChangeListener = (observable, oldValue, newValue) -> {
            nameValid = true;
            nameTextField.getStyleClass().remove("text-field-invalid");

            if (null != newValue) {
                Pattern pattern = Pattern.compile("\\S+");
                Matcher matcher = pattern.matcher(newValue);

                if (!matcher.matches()) {
                    nameValid = false;
                    nameTextField.getStyleClass().add("text-field-invalid");
                }
            } else {
                nameValid = false;
                nameTextField.getStyleClass().add("text-field-invalid");
            }
        };
        nameValue.addListener(nameChangeListener);

        relationshipChangeListener = (observable, oldValue, newValue) -> {
            relationshipValid = true;
            relationshipTextField.getStyleClass().remove("text-field-invalid");

            if (null != newValue) {
                Pattern pattern = Pattern.compile("\\S+");
                Matcher matcher = pattern.matcher(newValue);

                if (!matcher.matches()) {
                    relationshipValid = false;
                    relationshipTextField.getStyleClass().add("text-field-invalid");
                }
            } else {
                relationshipValid = false;
                relationshipTextField.getStyleClass().add("text-field-invalid");
            }
        };
        relationshipValue.addListener(relationshipChangeListener);

        telephoneChangeListener = (observable, oldValue, newValue) -> {
            telephoneValid = true;
            telephoneTextField.getStyleClass().remove("text-field-invalid");

            if (null != newValue) {
                Pattern pattern = Pattern.compile("\\d{10}");
                Matcher matcher = pattern.matcher(newValue);

                if (!matcher.matches()) {
                    telephoneValid = false;
                    telephoneTextField.getStyleClass().add("text-field-invalid");
                }
            } else {
                telephoneValid = false;
                telephoneTextField.getStyleClass().add("text-field-invalid");
            }
        };
        telephoneValue.addListener(telephoneChangeListener);

        addressChangeListener = (observable, oldValue, newValue) -> {
            addressValid = true;
            addressTextField.getStyleClass().remove("text-field-invalid");

            if (null != newValue) {
                Pattern pattern = Pattern.compile("\\S+");
                Matcher matcher = pattern.matcher(newValue);

                if (!matcher.matches()) {
                    addressValid = false;
                    addressTextField.getStyleClass().add("text-field-invalid");
                }
            } else {
                addressValid = false;
                addressTextField.getStyleClass().add("text-field-invalid");
            }
        };
        addressValue.addListener(addressChangeListener);
    }

    @Override
    public boolean isValid() {
        return (nameValue.isEmpty().get() && relationshipValue.isEmpty().get() && telephoneValue.isEmpty().get() && addressValue.isEmpty().get())
                || (nameValid && relationshipValid && telephoneValid && addressValid);
    }

    @Override
    public void refreshState() {
        nameChangeListener.changed(null, null, nameTextField.getText());
        relationshipChangeListener.changed(null, null, relationshipTextField.getText());
        telephoneChangeListener.changed(null, null, telephoneTextField.getText());
        addressChangeListener.changed(null, null, addressTextField.getText());
    }

    @Override
    public Notification getInvalidArgumentNotification() {
        return NotificationFactory.defaultInvalidArguementNotification("Incorrect emergency contact information",
                "Please enter a correct set of emergency contact information");
    }
}
