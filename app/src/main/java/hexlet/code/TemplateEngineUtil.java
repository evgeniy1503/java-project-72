//package hexlet.code;
//
//import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
//import org.thymeleaf.TemplateEngine;
//import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
//import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
//
//public class TemplateEngineUtil {
//
//    public static TemplateEngine createTemplateEngine() {
//
//        TemplateEngine templateEngine = new TemplateEngine();
//        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
//        templateResolver.setPrefix("/templates/");
//
//        templateEngine.addTemplateResolver(templateResolver);
//        templateEngine.addDialect(new LayoutDialect());
//        templateEngine.addDialect(new Java8TimeDialect());
//
//        return templateEngine;
//    }
//}
