package com.packagename.myapp;


import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Locale;

@Route
@PWA(name = "Vaadin Application",
        shortName = "Vaadin App",
        description = "This is an example Vaadin application.",
        enableInstallPrompt = false)
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")

@JavaScript("./script.js")
public class MainView extends VerticalLayout  {

    /**
     * Construct a new Vaadin view.
     * <p>
     * Build the initial UI state for the user accessing the application.
     *
     * @param service The message service. Automatically injected Spring managed bean.
     */
    public MainView(@Autowired GreetService service) {

        // Use TextField for standard text input
        //TextField textField = new TextField("Start Analysis");
        //textField.addThemeName("bordered");
        // Button click listeners can be defined as lambda expressions
        Locale finnishLocale = new Locale("EN", "EGYPT");
        DatePicker startDateTimePicker = new DatePicker("Start date ");
        DatePicker endDateTimePicker = new DatePicker("End date");
        startDateTimePicker.setPlaceholder("MM/DD/YYYY");
        endDateTimePicker.setPlaceholder("MM/DD/YYYY");
        endDateTimePicker.setLocale(finnishLocale);


        Button button = new Button("Start Analysis");

        // Theme variants give you predefined extra styles for components.
        // Example: Primary button has a more prominent look.
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        // You can specify keyboard shortcuts for buttons.
        // Example: Pressing enter in this view clicks the Button.
        button.addClickShortcut(Key.ENTER);

        // Use custom CSS classes to apply styling. This is defined in shared-styles.css.
        addClassName("centered-content");


        com.vaadin.flow.component.html.Label l = new com.vaadin.flow.component.html.Label();
        l.setText(" ");

        button.addClickListener(event -> {
            button.getStyle().set("backgroundColor","red");
            LocalDate start = startDateTimePicker.getValue();
            LocalDate end =endDateTimePicker.getValue();
            Notification notification = new Notification();
            // System.out.println(start+"-----"+end);
            if (start == null || end ==null ){

                System.out.println("enteredd ");
                Notification notif = new Notification();
                notif.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notif.setPosition(Notification.Position.TOP_CENTER);
                Div text = new Div(new Text("Please Fill Date field"));

                Button closeButton = new Button(new Icon("lumo", "cross"));
                closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
                closeButton.getElement().setAttribute("aria-label", "Close");
                closeButton.addClickListener(c -> {
                    notif.close();
                });

                HorizontalLayout layout = new HorizontalLayout(text, closeButton);
                layout.setAlignItems(Alignment.CENTER);
                notif.add(layout);
                notif.open();

            }else {

                GetAnalysis analysis = new GetAnalysis();
                int startMonth = start.getMonthValue();
                int startDay = start.getDayOfMonth();
                int endDay = end.getDayOfMonth();
                int endMonth = end.getMonthValue();
                // System.out.println(startDay+" "+startMonth+" "+endDay+" "+endMonth);
                int startDayYear = startDay+(startMonth-1)*30;
                int endDayYear = endDay+(endMonth-1)*30;
                String data = analysis.getData(startDayYear, endDayYear);
                String[] records= data.split("\n");
//                ListItem item= new ListItem("Hello World");
//                item.add(new ListItem("Hello again"));
//                item.setClassName("xx");
//                add(item);
                add(new Label("____________________________________________________________________"));
                for(String record:records){
                    String recData=  record ;
                    String serviceName= recData.split(":")[0];
                    ListItem label =new ListItem(serviceName);
                    label.setClassName("serviceName");
                    label.setWidth("80em");
                    add(label);

                    String[] dataOfService= record.split(" ");
                    for(int i= 1 ;i<dataOfService.length; i++){
                        String util= dataOfService[i];
                        ListItem item= new ListItem(util);
                        item.setClassName("serviceData");
                        item.setWidth("80em");
                        add(item);
                    }

                }

//                l.getStyle().set("fontWeight", "bold");
//                l.setText(data);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                notification.setText("Success");
                notification.open();
                notification.setDuration(1500);
            }
            button.getStyle().set("backgroundColor", "blue");
        });




        add(startDateTimePicker,endDateTimePicker);
        add(button,l);

    }

}