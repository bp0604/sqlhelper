package com.jn.sqlhelper.cli.commands;

import com.jn.langx.util.Emptys;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.StringMap;
import com.jn.langx.util.function.Consumer2;
import com.jn.sqlhelper.common.connection.ConnectionConfiguration;
import com.jn.sqlhelper.common.connection.DirectoryPropertiesFileConfigurationRepository;
import com.jn.sqlhelper.common.connection.NamedConnectionConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.List;
import java.util.Properties;

@ShellComponent
public class JdbcConnectionCommands {

    @Autowired
    DirectoryPropertiesFileConfigurationRepository repository;

    @ShellMethod(key = "jdbc list", value = "list all connection configuration names")
    public List<String> getConnectionNames() {
        return Collects.asList(repository.getAll().keySet());
    }

    @ShellMethod(key = "jdbc create", value = "create an new connection configuration")
    public ConnectionConfiguration addConnection(
            @ShellOption() String name,
            @ShellOption() String driver,
            @ShellOption() String url,
            @ShellOption String username,
            @ShellOption(defaultValue = "") String password,
            @ShellOption(defaultValue = "") String props) {

        Preconditions.checkTrue(Strings.isNotEmpty(name));
        Preconditions.checkTrue(Strings.isNotEmpty(driver));
        Preconditions.checkTrue(Strings.isNotEmpty(url));
        Preconditions.checkTrue(Strings.isNotEmpty(username));


        StringMap propsMap = new StringMap(props, "=", "&");
        final Properties properties = new Properties();
        Collects.forEach(propsMap, new Consumer2<String, String>() {
            @Override
            public void accept(String key, String value) {
                properties.setProperty(key, value);
            }
        });

        NamedConnectionConfiguration configuration = new NamedConnectionConfiguration();
        configuration.setName(name);
        configuration.setDriver(driver);
        configuration.setUrl(url);
        configuration.setUser(username);
        configuration.setPassword(password);
        configuration.setDriverProps(properties);

        repository.add(configuration);
        return configuration;
    }


}