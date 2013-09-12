package com.bdcom.dce.view.resource;

import com.bdcom.dce.biz.pojo.Scenario;
import com.bdcom.dce.biz.scenario.ScenarioUtil;
import com.bdcom.dce.biz.storage.Item;
import com.bdcom.dce.sys.ApplicationConstants;
import com.bdcom.dce.util.LocaleUtil;
import com.bdcom.dce.util.StringUtil;
import com.bdcom.dce.view.util.GBC;
import com.bdcom.dce.view.util.LimitedDocument;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-9-9    <br/>
 * Time: 14:23  <br/>
 */
public class ScenarioEditDialog extends JDialog implements ItemListener,
        ApplicationConstants {

    private static final long serialVersionUID = -8199220889481822853L;
    private static final String CONFIRM = "confirm";
    private static final String CANCEL = "cancel";
    private static final String SCENARIO_ATTR_SELECT = "Scenario Attribute Selection";
    private static final String ATTR_CONTENT =  "Attribute Content";
    private static final String BASIC_INFO = "Basic Info";

    private final JComponent fatherFrame;

    private JDialog dialog = this;
    private Map<String, JLabel> labelGroup;
    private Map<String, JTextField> textFieldGroup;
    private Map<String, JCheckBox> checkBoxGroup;
    private Map<JCheckBox, String> checkBoxNames;
    private JCheckBox[] attrCBoxes;
    private Set<String> selectedAttrSet;

    private JButton confirmBt;
    private JButton cancelBt;
    private ActionListener confirmAction;

    private JPanel basicPane;
    private JPanel attrSelectPane;
    private JPanel attrInputPane;
    private JPanel buttonPane;

    public ScenarioEditDialog(JComponent fatherFrame) {
        this.fatherFrame = fatherFrame;
        initUI();
    }

    private void initUI() {
        String selection = LocaleUtil.getLocalName( SCENARIO_ATTR_SELECT );
        String content = LocaleUtil.getLocalName( ATTR_CONTENT );

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                if (null != fatherFrame) {
                    fatherFrame.setEnabled(true);
                }
            }
        });

        basicPane = new JPanel();
        attrSelectPane = new JPanel();
        attrInputPane = new JPanel();
        buttonPane = new JPanel();
        basicPane.setLayout( new GridBagLayout() );
        attrSelectPane.setLayout( new GridBagLayout() );
        attrInputPane.setLayout( new GridBagLayout() );
        buttonPane.setLayout( new GridBagLayout() );

        basicPane.setPreferredSize( new Dimension( 600, 100 ) );
        attrSelectPane.setPreferredSize( new Dimension( 600, 200 ) );
        attrInputPane.setPreferredSize(new Dimension( 600, 250 ) );

        Border selectionBorder = BorderFactory.createTitledBorder( selection );
        Border contentBorder = BorderFactory.createTitledBorder( content );
        attrSelectPane.setBorder( selectionBorder );
        attrInputPane.setBorder( contentBorder );

        String[] attrArray = ScenarioUtil.getFocusedAttrs();
        initLabelGroup(attrArray);
        initTextFieldGroup(attrArray);
        initCheckBoxGroup(attrArray);
        initButtons();
        initBasicPaneLayout();
        initAttrSelectPaneLayout();
        initButtonLayout();

        setLayout( new GridBagLayout() );
        add( basicPane, new GBC(0, 0) );
        add( attrSelectPane, new GBC(0, 1) );
        add( attrInputPane, new GBC(0, 2) );
        add( buttonPane, new GBC(0, 3) );
    }

    private void initBasicPaneLayout() {
        String basicInfo = LocaleUtil.getLocalName( BASIC_INFO );
        Border titledBorder = BorderFactory.createTitledBorder( basicInfo );
        basicPane.setBorder( titledBorder );

        JLabel scenarioNameLabel = labelGroup.get(SCENARIO_NAME);
        JLabel serialLabel = labelGroup.get(MATCH_SERIAL);
        JLabel beginIndexLabel =  labelGroup.get(BEGIN_INDEX);
        JTextField scenarioNameField = textFieldGroup.get(SCENARIO_NAME);
        JTextField serialField = textFieldGroup.get(MATCH_SERIAL);
        JTextField beginIndexField = textFieldGroup.get(BEGIN_INDEX);

        basicPane.add(scenarioNameLabel, new GBC(0, 0).setAnchor(GBC.WEST)
                .setInsets(8, 8, 8, 8)
                .setFill(GBC.HORIZONTAL));
        basicPane.add( scenarioNameField, new GBC(1, 0)
                .setAnchor(GBC.WEST)
                .setInsets(8, 8, 8, 8) );
        basicPane.add( serialLabel, new GBC(2, 0).setAnchor(GBC.WEST)
                .setInsets(8, 8, 8, 8)
                .setFill(GBC.HORIZONTAL) );
        basicPane.add( serialField, new GBC(3, 0).setAnchor(GBC.WEST)
                .setInsets(8, 8, 8, 8)
                .setFill(GBC.HORIZONTAL) );
        basicPane.add( beginIndexLabel, new GBC(0, 1).setAnchor(GBC.WEST)
                .setInsets(8, 8, 8, 8)
                .setFill(GBC.HORIZONTAL) );
        basicPane.add(beginIndexField, new GBC(1, 1)
                .setAnchor(GBC.WEST)
                .setInsets(8, 8, 8, 8) );

    }

    private void initLabelGroup(String[] attrs) {
        if ( null == attrs || 0 == attrs.length ) {
            return;
        }
        if ( null == labelGroup ) {
            labelGroup = new HashMap<String, JLabel>( attrs.length + 3 );
            for (String attr : attrs ) {
                JLabel label = new JLabel();
                label.setPreferredSize( new Dimension(80,20) );
                label.setText(
                        getLocalName(attr)
                );
                labelGroup.put(attr, label);
            }
            JLabel sNameLabel = new JLabel();
            JLabel sNumLabel = new JLabel();
            JLabel indexLabel = new JLabel();
            sNameLabel.setPreferredSize(new Dimension(80, 20));
            sNameLabel.setText(getLocalName(SCENARIO_NAME));
            sNumLabel.setText(getLocalName(MATCH_SERIAL));
            indexLabel.setText(getLocalName(BEGIN_INDEX));
            labelGroup.put(SCENARIO_NAME, sNameLabel);
            labelGroup.put(MATCH_SERIAL, sNumLabel);
            labelGroup.put(BEGIN_INDEX, indexLabel);
        }
    }

    private void initTextFieldGroup(String[] attrs) {
        if ( null == attrs || 0 == attrs.length ) {
            return;
        }
        if ( null == textFieldGroup ) {
            textFieldGroup = new HashMap<String, JTextField>(attrs.length + 3);
            for (String attr : attrs) {
                JTextField jtf = new JTextField();
                jtf.setPreferredSize(new Dimension(160,20));
                textFieldGroup.put(attr, jtf);
            }
            final JTextField scenarioNameField = new JTextField();
            final JTextField serialField = new JTextField();
            final JTextField beginIndexField = new JTextField();

            beginIndexField.setDocument(new LimitedDocument(LimitedDocument.NUMBER, 2));

            scenarioNameField.setPreferredSize(new Dimension(160, 20));
            serialField.setPreferredSize(new Dimension(160, 20));
            beginIndexField.setPreferredSize(new Dimension(160, 20));

            scenarioNameField.setForeground( Color.blue );
            serialField.setForeground(Color.blue);
            beginIndexField.setForeground(Color.blue);

            textFieldGroup.put(SCENARIO_NAME, scenarioNameField);
            textFieldGroup.put(MATCH_SERIAL, serialField);
            textFieldGroup.put(BEGIN_INDEX, beginIndexField);
        }
    }

    private void initCheckBoxGroup(String[] attrs) {
        if ( null == attrs || 0 == attrs.length ) {
            return;
        }
        if ( null == checkBoxNames) {
            int len = attrs.length;
            attrCBoxes = new JCheckBox[len];
            checkBoxNames = new HashMap<JCheckBox, String>(len);
            checkBoxGroup = new HashMap<String, JCheckBox>(len);
            for ( int i = 0; i < len; i++ ) {
                JCheckBox jcb = new JCheckBox( getLocalName( attrs[i] ) );
                jcb.setSelected( false );
                jcb.addItemListener(this);
                checkBoxNames.put(jcb, attrs[i]);
                checkBoxGroup.put( attrs[i], jcb );
                attrCBoxes[i] = jcb;
            }
        }
    }

    private void initAttrSelectPaneLayout() {
        int index = 0;
        for (JCheckBox jcb : attrCBoxes ) {
            attrSelectPane.add(jcb, new GBC(index % 4, index / 4)
                    .setInsets(5, 10, 5, 10)
                    .setAnchor(GBC.WEST)
            );
            index ++;
        }
    }

    private void initButtons() {
        String cancel = LocaleUtil.getLocalName(CANCEL);
        cancelBt = new JButton( cancel );
        cancelBt.setPreferredSize( new Dimension(70, 30) );
        cancelBt.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });

        String confirm = LocaleUtil.getLocalName( CONFIRM );
        confirmBt = new JButton( confirm );
        confirmBt.setPreferredSize(new Dimension(70, 30));
    }

    private void initButtonLayout() {
        buttonPane.add( confirmBt, new GBC(0, 0).setInsets( 10 ) );
        buttonPane.add( cancelBt, new GBC(1, 0).setInsets( 10 ) );
    }

    private void updateTextFieldGroup(Scenario scenario) {
        if ( null == scenario ) {
            return;
        }
        Set<String> attrSet = scenario.getAttrNames();
        for ( String attr: attrSet ) {
            JTextField jtf = textFieldGroup.get( attr );
            String value = scenario.getAttr(attr);
            if ( null != jtf && !value.equals(jtf.getText()) ) {
                jtf.setText(value);
            }
        }
    }

    private void updateCheckBoxGroup(Scenario scenario) {
        if ( null == scenario ) {
            return;
        }
        Set<String> attrSet = scenario.getAttrNames();
        for ( String attr: attrSet ) {
            JCheckBox jcb = checkBoxGroup.get( attr );
            if ( null != jcb && !jcb.isSelected() ) {
                jcb.setSelected( true );
            }
        }
    }

    private void updateAttrInputPane(Set<String> selectedAttrSet) {
        attrInputPane.removeAll();
        try {
            if ( null == selectedAttrSet || selectedAttrSet.isEmpty() ) {
                return;
            }

            int location = 0;
            for (String attr : selectedAttrSet ) {
                JLabel label = labelGroup.get(attr);
                JTextField field = textFieldGroup.get(attr);
                if ( null == label || null == field ) {
                    continue;
                }
                if (location % 2 == 0) {
                    attrInputPane.add(label,
                            new GBC(0 , location).setAnchor(GBC.WEST)
                                    .setInsets(4, 8, 4, 8)
                                    .setFill(GBC.HORIZONTAL)
                    );
                    attrInputPane.add(field,
                            new GBC(1 , location).setAnchor(GBC.WEST)
                                    .setInsets(4, 8, 4, 8)
                    );
                } else {
                    attrInputPane.add(label,
                            new GBC(2, location -1).setAnchor(GBC.WEST)
                                    .setInsets(4, 8, 4, 8)
                                    .setFill(GBC.HORIZONTAL)
                    );
                    attrInputPane.add(field,
                            new GBC(3, location -1).setAnchor(GBC.WEST)
                                    .setInsets(4, 8, 4, 8)
                    );
                }
                location ++ ;
            }
        } finally {
            attrInputPane.revalidate();
        }
    }

    private void updateItemAttr(Item i) {
        JTextField nameField = textFieldGroup.get( SCENARIO_NAME );
        JTextField serialField = textFieldGroup.get( MATCH_SERIAL );
        JTextField beginIndexField = textFieldGroup.get( BEGIN_INDEX );

        String remarkName = i.getRemarkName();
        String serial = i.getSerial();
        String beginIndex = String.valueOf(i.getBeginIndex());

        if ( !remarkName.equals( nameField.getText() ) ) {
            nameField.setText( remarkName );
        }
        if ( !serial.equals( serialField.getText() ) ) {
            serialField.setText( serial );
        }
        if ( !beginIndex.equals( beginIndexField.getText() ) ) {
            beginIndexField.setText( beginIndex );
        }
    }

    public void update(Scenario scenario) {
        if ( null == scenario ) {
            clear();
            return;
        }
        clear();
        updateItemAttr(scenario);
        updateTextFieldGroup(scenario);
        updateCheckBoxGroup(scenario);

        Set<String> attrNames = scenario.getAttrNames();
        if ( null == selectedAttrSet ) {
             selectedAttrSet = new HashSet<String>();
        } else {
            selectedAttrSet.clear();
        }
        selectedAttrSet.addAll( attrNames );
        updateAttrInputPane(attrNames);
    }

    public void clear() {
        if ( null != selectedAttrSet
                && !selectedAttrSet.isEmpty() ) {
            selectedAttrSet.clear();
        }
        int len = attrCBoxes.length;
        for ( int i = 0; i < len; i++ ) {
            JCheckBox jcb = attrCBoxes[i];
            if ( jcb.isSelected() ) {
                jcb.setSelected( false );
            }
            String attr = checkBoxNames.get( jcb );
            JTextField jtf = textFieldGroup.get( attr );
            if ( null != jtf && !"".equals( jtf.getText() ) ) {
                jtf.setText( "" );
            }
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        int len = attrCBoxes.length;
        if ( null == selectedAttrSet ) {
            selectedAttrSet = new HashSet<String>( len );
        }
        for ( int i = 0; i < len; i++ ) {
            JCheckBox jcb = attrCBoxes[i];
            String attr = checkBoxNames.get( jcb );
            if ( jcb.isSelected() && !selectedAttrSet.contains(attr) ) {
                selectedAttrSet.add( attr );
            } else if ( !jcb.isSelected() && selectedAttrSet.contains(attr) ) {
                selectedAttrSet.remove( attr );
            }
        }
        updateAttrInputPane( selectedAttrSet );
    }

    public void setConfirmAction(ActionListener al) {
        if ( null != confirmAction ) {
            confirmBt.removeActionListener( confirmAction );
        }
        confirmAction = al;
        confirmBt.addActionListener( al );
    }

    public Scenario gatherAsScenario() {
        return gatherAsScenario( new Scenario() );
    }

    public Scenario gatherAsScenario(Scenario scenario) {
        JTextField nameField = textFieldGroup.get( SCENARIO_NAME );
        JTextField serialField = textFieldGroup.get( MATCH_SERIAL );
        JTextField beginIndexField = textFieldGroup.get( BEGIN_INDEX );

        String remarkName = nameField.getText();
        String serial = serialField.getText();
        String indexStr = beginIndexField.getText();
        int beginIndex = 0;
        if ( StringUtil.isNotBlank( indexStr ) ) {
            beginIndex = Integer.parseInt( indexStr );
        }

        scenario.setRemarkName( remarkName);
        scenario.setSerial( serial );
        scenario.setBeginIndex( beginIndex );

        for( String attr : selectedAttrSet ) {
            JTextField jtf = textFieldGroup.get( attr );
            if ( null != jtf ) {
                String value = jtf.getText();
                if (StringUtil.isNotBlank( value )) {
                    scenario.putAttr( attr, value );
                }
            }
        }

        return scenario;
    }

    public void display() {
        if ( null != fatherFrame ) {
            fatherFrame.setEnabled( false );
        }
        dialog.setAlwaysOnTop( true );
        dialog.pack();
        dialog.setResizable( false );
        dialog.setLocationRelativeTo( null );
        dialog.setVisible( true );
    }

    public void close() {
        if ( null != fatherFrame ) {
            fatherFrame.setEnabled( true );
        }
        dialog.setVisible( false );
    }

    private String getLocalName(String name ) {
        return LocaleUtil.getLocalName( name );
    }

}
