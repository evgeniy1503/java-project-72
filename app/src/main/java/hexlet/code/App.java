package hexlet.code;

import hexlet.code.controllers.WelcomeController;
import io.javalin.plugin.rendering.template.JavalinThymeleaf;
import io.javalin.Javalin;
import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;


public  class App {

    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "5000");
        return Integer.valueOf(port);
    }

    private static TemplateEngine createTemplateEngine() {

        TemplateEngine templateEngine = new TemplateEngine();
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("/templates/");

        templateEngine.addTemplateResolver(templateResolver);
        templateEngine.addDialect(new LayoutDialect());
        templateEngine.addDialect(new Java8TimeDialect());

        return templateEngine;
    }

    private static void addRoutes(Javalin app) {
        app.get("/", WelcomeController.welcome);
    }

    public static Javalin getApp() {
        Javalin app = Javalin.create(config -> {
            config.enableDevLogging();
            JavalinThymeleaf.configure(createTemplateEngine());
        });

        addRoutes(app);

        return app;
    }

    public static void main(String[] args) {
        Javalin app = getApp();
        app.start(getPort());
    }
}
