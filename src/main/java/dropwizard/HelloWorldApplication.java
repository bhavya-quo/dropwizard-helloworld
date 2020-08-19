package dropwizard;


import java.util.Map;

import dropwizard.cli.RenderCommand;
import dropwizard.core.Person;
import dropwizard.core.Template;
import dropwizard.db.PersonDAO;
import dropwizard.health.TemplateHealthCheck;
import dropwizard.resources.FilteredResource;
import dropwizard.resources.HelloWorldResource;
import dropwizard.resources.PeopleResource;
import dropwizard.resources.PersonResource;
import dropwizard.resources.ViewResource;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;


public class HelloWorldApplication extends Application<HelloWorldConfiguration> {

    public static void main(final String[] args) throws Exception {
        new HelloWorldApplication().run(args);
    }

    private final HibernateBundle<HelloWorldConfiguration> hibernateBundle =
      new HibernateBundle<HelloWorldConfiguration>(Person.class) {
          @Override
          public DataSourceFactory getDataSourceFactory(HelloWorldConfiguration configuration) {
              return configuration.getDataSourceFactory();
          }
      };


    @Override
    public String getName() {
        return "demo";
    }

    @Override
    public void initialize(final Bootstrap<HelloWorldConfiguration> bootstrap) {
        // Enable variable substitution with environment variables
        bootstrap.setConfigurationSourceProvider(
          new SubstitutingSourceProvider(
            bootstrap.getConfigurationSourceProvider(),
            new EnvironmentVariableSubstitutor(false)
          )
        );
        bootstrap.addCommand(new RenderCommand());
        bootstrap.addBundle(new AssetsBundle());
        bootstrap.addBundle(new MigrationsBundle<HelloWorldConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(HelloWorldConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });
        bootstrap.addBundle(hibernateBundle);
        bootstrap.addBundle(new ViewBundle<HelloWorldConfiguration>() {
            @Override
            public Map<String, Map<String, String>> getViewConfiguration(HelloWorldConfiguration configuration) {
                return configuration.getViewRendererConfiguration();
            }
        });
    }

    @Override
    public void run(final HelloWorldConfiguration configuration,
                    final Environment environment) {
        final Template template = configuration.buildTemplate();
        environment.healthChecks().register("template", new TemplateHealthCheck(template));
        final PersonDAO dao = new PersonDAO(hibernateBundle.getSessionFactory());
        environment.jersey().register(new HelloWorldResource(template));
        environment.jersey().register(new ViewResource());
        environment.jersey().register(new PeopleResource(dao));
        environment.jersey().register(new PersonResource(dao));
        environment.jersey().register(new FilteredResource());
    }

}
