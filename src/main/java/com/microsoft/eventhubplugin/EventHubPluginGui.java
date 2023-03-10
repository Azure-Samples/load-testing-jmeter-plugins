package com.microsoft.eventhubplugin;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.BorderFactory;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jorphan.gui.JLabeledTextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microsoft.eventhubplugin.EventHubPlugin;

public class EventHubPluginGui extends AbstractSamplerGui implements ChangeListener {
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(EventHubPluginGui.class);

    private JPanel panel;
    private JLabeledTextField eventHubConnectionStringVarName;
    private JLabeledTextField eventHubName;
    private JLabeledTextField liqidTemplateFileName;

    public EventHubPluginGui() {
        init();
    }

    @Override
    public void configure(TestElement element) {
        super.configure(element);
        eventHubConnectionStringVarName
                .setText(element.getPropertyAsString(EventHubPlugin.EVENT_HUB_CONNECTION_VAR_NAME));
        eventHubName.setText(element.getPropertyAsString(EventHubPlugin.EVENT_HUB_NAME));
        liqidTemplateFileName.setText(element.getPropertyAsString(EventHubPlugin.LIQUID_TEMPLATE_FILENAME));
    }

    @Override
    public TestElement createTestElement() {
        EventHubPlugin sampler = new EventHubPlugin();
        modifyTestElement(sampler);
        return sampler;
    }

    @Override
    public void modifyTestElement(TestElement sampler) {
        sampler.clear();
        super.configureTestElement(sampler);
        sampler.setProperty(EventHubPlugin.EVENT_HUB_CONNECTION_VAR_NAME, eventHubConnectionStringVarName.getText());
        sampler.setProperty(EventHubPlugin.EVENT_HUB_NAME, eventHubName.getText());
        sampler.setProperty(EventHubPlugin.LIQUID_TEMPLATE_FILENAME, liqidTemplateFileName.getText());
    }

    @Override
    public void clearGui() {
        super.clearGui();

        eventHubConnectionStringVarName.setText("");
        eventHubName.setText("");
        liqidTemplateFileName.setText("");
    }

    @Override
    public String getLabelResource() {
        return getClass().getName();
    }

    public String getStaticLabel() {
        return "Azure Event Hubs Sampler";
    }

    private JPanel createEventHubConnectionStringVarNamePanel() {
        eventHubConnectionStringVarName = new JLabeledTextField("Event Hubs Connection String Var Name:");
        eventHubConnectionStringVarName.setName(EventHubPlugin.EVENT_HUB_CONNECTION_VAR_NAME);

        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.add(eventHubConnectionStringVarName);

        return panel;
    }

    private JPanel createEventHubNamePanel() {
        eventHubName = new JLabeledTextField("Event Hubs Name:");
        eventHubName.setName(EventHubPlugin.EVENT_HUB_NAME);

        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.add(eventHubName);

        return panel;
    }

    private JPanel createLiqidTemplateFileNamePanel() {
        liqidTemplateFileName = new JLabeledTextField("Liqid Template File Name:");
        liqidTemplateFileName.setName(EventHubPlugin.LIQUID_TEMPLATE_FILENAME);

        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.add(liqidTemplateFileName);

        return panel;
    }

    private void init() {
        setLayout(new BorderLayout(0, 5));
        setBorder(makeBorder());
        add(makeTitlePanel(), BorderLayout.NORTH);
        // MAIN PANEL
        VerticalPanel mainPanel = new VerticalPanel();
        VerticalPanel eventHubsConfigPanel = new VerticalPanel();
        eventHubsConfigPanel.setBorder(BorderFactory.createTitledBorder("Event Hubs Configuration"));
        eventHubsConfigPanel.add(createEventHubConnectionStringVarNamePanel());
        eventHubsConfigPanel.add(createEventHubNamePanel());
        eventHubsConfigPanel.add(createLiqidTemplateFileNamePanel());
        mainPanel.add(eventHubsConfigPanel, BorderLayout.NORTH);

        add(mainPanel, BorderLayout.CENTER);
    }

    @Override
    public void stateChanged(ChangeEvent event) {
    }

}