package ch.petikoch.tools.taskgraphtool.ui;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.*;
import com.vaadin.ui.declarative.Design;

/**
 * !! DO NOT EDIT THIS FILE !!
 *
 * This class is generated by Vaadin Designer and will be overwritten.
 *
 * Please make a subclass with logic and additional interfaces as needed,
 * e.g class LoginView extends LoginDesign implements View { }
 */
@DesignRoot
@AutoGenerated
@SuppressWarnings("serial")
public class DesignNodeConnectForm extends VerticalLayout {
    protected FormLayout formLayout;
    protected ComboBox<String> fromComboBox;
    protected TextField fromTextField;
    protected ComboBox<String> toComboBox;
    protected TextField toTextField;
    protected Button connectButton;

    public DesignNodeConnectForm() {
        Design.read(this);
    }
}
