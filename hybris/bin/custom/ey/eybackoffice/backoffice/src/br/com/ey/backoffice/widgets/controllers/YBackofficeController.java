
package br.com.ey.backoffice.widgets.controllers;

import com.hybris.cockpitng.util.DefaultWidgetController;
import org.zkoss.chart.Charts;
import org.zkoss.chart.model.CategoryModel;
import org.zkoss.chart.model.DefaultCategoryModel;
import org.zkoss.chart.model.DefaultPieModel;
import org.zkoss.chart.model.PieModel;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import org.zkoss.zul.Label;
//import ybackofficepackage.services.YBackofficeService;

public class YBackofficeController extends DefaultWidgetController {
    private static final long serialVersionUID = 1L;
    private Label label;
//    @WireVariable
//    private transient YBackofficeService ybackofficeService;

    @Wire
    Charts chart;

    @Wire
    Charts chart2;

    public YBackofficeController() {
    }

    public void initialize(Component comp) {
        super.initialize(comp);
        //this.label.setValue(this.ybackofficeService.getHello() + " EY Rodolfo");
        //this.label.setValue(" EY Rodolfo");

        CategoryModel model = new DefaultCategoryModel();

        // Set value to the model
        model.setValue("Electronics", "First Quarter", new Integer(11));
        model.setValue("Electronics", "Second Quarter", new Integer(20));
        model.setValue("Electronics", "Third Quarter", new Integer(16));
        model.setValue("Electronics", "Last Quarter", new Integer(-2));
        model.setValue("Power Tools", "First Quarter", new Integer(6));
        model.setValue("Power Tools", "Second Quarter", new Integer(12));
        model.setValue("Power Tools", "Third Quarter", new Integer(10));
        model.setValue("Power Tools", "Last Quarter", new Integer(2));

        // Set model to the chart
        chart.setModel(model);

        PieModel model2 = new DefaultPieModel();
        model2.setValue("302525", new Double(12.5));
        model2.setValue("252252", new Double(50.2));
        model2.setValue("456554", new Double(20.5));
        model2.setValue("123455", new Double(15.5));


        chart2.setModel(model2);


    }
}