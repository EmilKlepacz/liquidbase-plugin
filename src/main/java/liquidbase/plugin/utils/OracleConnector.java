package liquidbase.plugin.utils;


import liquidbase.plugin.action.function.FunRegisterSQL;
import liquidbase.plugin.action.module.ModRegisterSQL;
import liquidbase.plugin.action.module.Module;
import liquidbase.plugin.settings.AppSettingsState;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.*;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;


public class OracleConnector {
    static AppSettingsState settings = AppSettingsState.getInstance();

    private static String url = settings.JdbcUrl;
    private static String username = settings.dbUser;
    private static String password = settings.dbPassword;
    private static String ojdbcDriverPath = settings.ojdbcDriverPath;

    public static void loadOJDBCDriver() throws MalformedURLException {
        File driverFile = new File(ojdbcDriverPath);

        // Get the URL representing the driver JAR file
        URL driverUrl = driverFile.toURI().toURL();

        URLClassLoader classLoader = new URLClassLoader(new URL[]{driverUrl}, OracleConnector.class.getClassLoader());
        Driver driver;
        try {
            driver = (Driver) Class.forName("oracle.jdbc.driver.OracleDriver", true, classLoader).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        try {
            DriverManager.registerDriver(new DelegatingDriver(driver)); // register using the Delegating Driver
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static CompletableFuture<FunRegisterSQL> getApiFunctionRegisterSQL(
            String name,
            String shortDesc,
            int securityLevel,
            int implementation,
            int outputType,
            String apiModPath) {

        return CompletableFuture.supplyAsync(() -> {
            Connection connection = null;
            try {
                connection = DriverManager.getConnection(url, username, password);

                String sql = "select apifun#, apifun#register_sql from table(utlliqplu.get_register_fun_sql(?, ?, ?, ?, ?, ?))";
                try (CallableStatement callableStatement = connection.prepareCall(sql)) {
                    callableStatement.setString(1, name);
                    callableStatement.setString(2, shortDesc);
                    callableStatement.setInt(3, securityLevel);
                    callableStatement.setInt(4, implementation);
                    callableStatement.setInt(5, outputType);
                    callableStatement.setString(6, apiModPath);

                    try (ResultSet resultSet = callableStatement.executeQuery()) {

                        FunRegisterSQL funRegisterSQL = null;
                        // Iterate over the ResultSet and populate the HashMap
                        while (resultSet.next()) {
                            int apiFun = resultSet.getInt("apifun#");
                            Clob registerSQL = resultSet.getClob("apifun#register_sql");
                            funRegisterSQL = new FunRegisterSQL(apiFun, SQLUtils.clobToString(registerSQL));
                        }

                        return funRegisterSQL;
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            } finally {
                // Close the connection in a final block to ensure it's always closed
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public static CompletableFuture<ArrayList<Module>> getModules() {
        return CompletableFuture.supplyAsync(() -> {
            Connection connection = null;
            try {
                connection = DriverManager.getConnection(url, username, password);
                // Call function
                String sql = "select apimod#, apimod#parent, apimod#path from table(utlliqplu.get_apimod_tree)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        ArrayList<Module> modules = new ArrayList<>();

                        // Iterate over the ResultSet and populate the HashMap
                        while (resultSet.next()) {
                            int apimod = resultSet.getInt("apimod#");
                            int apimodParent = resultSet.getInt("apimod#parent");
                            String apimodPath = resultSet.getString("apimod#path");
                            modules.add(new Module(apimod, apimodParent, apimodPath));
                        }

                        return modules;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            } finally {
                // Close the connection in a final block to ensure it's always closed
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public static CompletableFuture<ArrayList<String>> getUnregisteredModules() {
        return CompletableFuture.supplyAsync(() -> {
            Connection connection = null;
            try {
                connection = DriverManager.getConnection(url, username, password);
                // Call function
                String sql = "select name from table(utlliqplu.get_unregistered_modules)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        ArrayList<String> unregisteredModules = new ArrayList<>();

                        // Iterate over the ResultSet and populate the HashMap
                        while (resultSet.next()) {
                            String name = resultSet.getString("name");
                            unregisteredModules.add(name);
                        }

                        return unregisteredModules;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            } finally {
                // Close the connection in a final block to ensure it's always closed
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public static CompletableFuture<ModRegisterSQL> getApiModuleRegisterSQL(String name) {

        return CompletableFuture.supplyAsync(() -> {
            Connection connection = null;
            try {
                connection = DriverManager.getConnection(url, username, password);

                String sql = "select apimod#, apimod#register_sql from table(utlliqplu.get_register_mod_sql(?))";
                try (CallableStatement callableStatement = connection.prepareCall(sql)) {
                    callableStatement.setString(1, name);

                    try (ResultSet resultSet = callableStatement.executeQuery()) {

                        ModRegisterSQL modRegisterSQL = null;
                        // Iterate over the ResultSet and populate the HashMap
                        while (resultSet.next()) {
                            int apiMod = resultSet.getInt("apimod#");
                            Clob registerSQL = resultSet.getClob("apimod#register_sql");
                            modRegisterSQL = new ModRegisterSQL(apiMod, SQLUtils.clobToString(registerSQL));
                        }

                        return modRegisterSQL;
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            } finally {
                // Close the connection in a final block to ensure it's always closed
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

}

