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
public class DesignSinglePageApplication extends HorizontalLayout {
    protected Button addNodeButton;
    protected Button addConnectionButton;
    protected Button deleteNodeButton;
    protected Button deleteConnectionButton;
    protected Button saveModelButton;
    protected Upload upload;
    protected Button resetButton;
    protected Button saveSvgButton;
    protected Button zoomIn;
    protected Button zoomOut;
    protected HorizontalSplitPanel splitPanel;
    protected HorizontalLayout imageWrapper;
    protected Label detailsLabel;
    protected VerticalLayout detailsPanel;

    public DesignSinglePageApplication() {
        Design.read(this);
    }
}
