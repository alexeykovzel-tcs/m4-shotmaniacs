package com.shotmaniacs.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Dao {

    private static final String HOST = "";
    private static final String DB_NAME = "";
    private static final String USERNAME = "";
    private static final String PASSWORD = "";

    /**
     * This methode creates a connection with the database.
     *
     * @return Connection that is established otherwise an error is thrown.
     */
    public Connection getConnection() {
        String url = "jdbc:postgresql://" + HOST + ":5432/" + DB_NAME + "?currentSchema=" + DB_NAME;
        try {
            return DriverManager.getConnection(url, USERNAME, PASSWORD);
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to connect to database: " + e.getMessage());
            return null;
        }
    }

    /**
     * This methode checks if a certain object exist in the database.
     *
     * @param table     contain the table name
     * @param attribute contain the attribute that needs to be exist in the database.
     * @param value     this is the value that the attribute should contain.
     * @return boolean if it is existing or not.
     */
    protected boolean ifExists(String table, String attribute, Object value) {
        try (Connection connection = getConnection()) {
            if (connection != null) {
                PreparedStatement ps = connection.prepareStatement(String.format(
                        "SELECT 1 FROM %s WHERE %s = ?", table, attribute));
                ps.setObject(1, value);
                return ps.executeQuery().next();
            }
        } catch (SQLException e) {
            System.out.println("Failed to execute SQL statement: " + e.getMessage());
        }
        return false;
    }

    /**
     * this methode insert query in that database, it automatically sanitized the input.
     *
     * @param className this is the className where that object needs to be into.
     * @param objects this are all the names with the attribute with all values.
     *                  The idea is to us 0,2,4,6,8,.. for the attribute name.
     *                  and the 1,3,5,7,9,11,.. for the value of the attribute.
     */
    protected boolean insertQuery(String className, Object... objects) {
        try (Connection connection = getConnection()) {
            //Here I test if the attribute are an even number.

            if (connection != null && objects.length % 2 == 0) {
                StringBuilder query = new StringBuilder();
                //INSERT INTO table_name (column1, column2, column3, ...)
                //VALUES (value1, value2, value3, ...);
                query.append(String.format("INSERT INTO %S (", className)).append(objects[0]);
                for (int i = 2; i < objects.length; i = i + 2) {
                    query.append(",").append(objects[i]);
                }
                query.append(") VALUES(?");
                query.append(",?".repeat(objects.length / 2 - 1));
                query.append(");");
                PreparedStatement statement = connection.prepareStatement(query.toString());
                int k = 1;
                for (int i = 1; i < objects.length; i = i + 2) {
                    //Special care about the data, this is a bug in sql. This is how you manually solve it.
                    Object obj = objects[i];
                    if (obj instanceof Date) {
                        statement.setTimestamp(k, new Timestamp(((Date) obj).getTime()));
                    } else {
                        statement.setObject(k, obj);
                    }
                    k++;
                }
                System.out.println("Executing: " + statement);
                return executeQuery(statement);
            } else {
                return false;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    /**
     * This methode updates an object in sql.
     *
     * @param className      the className were it needs to be updated.
     * @param attributeSet   the attribute name to be set.
     * @param valueSet       the value of the attributeSet.
     * @param attributeWhere the attribute name for the where clause.
     * @param valueWhere     the value of the attribute in the where clause.
     * @return boolean if the transaction did succeed.
     */
    protected boolean updateQuery(String className, String attributeSet, Object valueSet, String attributeWhere, Object valueWhere) {
        try (Connection connection = getConnection()) {
            if (connection != null) {
                PreparedStatement statement = connection.prepareStatement(String.format("UPDATE %s SET %s = ? WHERE %s=?", className, attributeSet, attributeWhere));
                if (valueSet instanceof Date) {
                    statement.setTimestamp(1, new Timestamp(((Date) valueSet).getTime()));
                } else {
                    statement.setObject(1, valueSet);
                }
                if (valueWhere instanceof Date) {
                    statement.setTimestamp(2, new Timestamp(((Date) valueWhere).getTime()));
                } else {
                    statement.setObject(2, valueWhere);
                }
                return executeQuery(statement);
            } else {
                return false;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    /**
     * This methode updates an object in sql with two properties.
     *
     * @param className       the className were it needs to be updated.
     * @param attributeSet    the attribute name to be set.
     * @param valueSet        the value of the attributeSet.
     * @param attributeWhere1 the attribute name for the where clause.
     * @param valueWhere1     the value of the attribute in the where clause.
     * @param attributeWhere1 the attribute name for the where clause.
     * @param valueWhere1     the value of the attribute in the where clause.
     * @return boolean if the transaction did succeed.
     */
    protected boolean updateQuery(String className, String attributeSet, Object valueSet, String attributeWhere1, Object valueWhere1, String attributeWhere2, Object valueWhere2) {
        try (Connection connection = getConnection()) {
            if (connection != null) {
                PreparedStatement statement = connection.prepareStatement(String.format("UPDATE %s SET %s = ? WHERE %s=? AND %s=?;", className, attributeSet, attributeWhere1, attributeWhere2));
                statement.setObject(1, valueSet);
                statement.setObject(2, valueWhere1);
                statement.setObject(3, valueWhere2);
                if (valueSet instanceof Date) {
                    statement.setTimestamp(1, new Timestamp(((Date) valueSet).getTime()));
                } else {
                    statement.setObject(1, valueSet);
                }
                if (valueWhere1 instanceof Date) {
                    statement.setTimestamp(2, new Timestamp(((Date) valueWhere1).getTime()));
                } else {
                    statement.setObject(2, valueWhere1);
                }
                if (valueWhere2 instanceof Date) {
                    statement.setTimestamp(2, new Timestamp(((Date) valueWhere2).getTime()));
                } else {
                    statement.setObject(2, valueWhere2);
                }
                return executeQuery(statement);
            } else {
                return false;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    /**
     * This methode deletes an object in sql
     *
     * @param className      the className were it needs to be updated.
     * @param attributeWhere the attribute name for the where clause.
     * @param valueWhere     the value of the attribute in the where clause.
     * @return boolean if the transaction did succeed.
     */
    protected boolean deleteQuery(String className, String attributeWhere, Object valueWhere) {
        try (Connection connection = getConnection()) {
            if (connection != null) {
                PreparedStatement statement = connection.prepareStatement(String.format("DELETE FROM %s WHERE %s = ?", className, attributeWhere));
                statement.setObject(1, valueWhere);
                return executeQuery(statement);
            } else {
                return false;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    /**
     * This methode deletes an object in sql with two properties.
     *
     * @param className       the className were it needs to be updated.
     * @param attributeWhere1 the attribute name for the where clause.
     * @param valueWhere1     the value of the attribute in the where clause.
     * @param attributeWhere1 the attribute name for the where clause.
     * @param valueWhere1     the value of the attribute in the where clause.
     * @return boolean if the transaction did succeed.
     */
    protected boolean deleteQuery(String className, String attributeWhere1, Object valueWhere1, String attributeWhere2, Object valueWhere2) {
        try (Connection connection = getConnection()) {
            if (connection != null) {
                PreparedStatement statement = connection.prepareStatement(String.format("DELETE FROM %s WHERE %s = ? AND %s = ?;", className, attributeWhere1, attributeWhere2));
                statement.setObject(1, valueWhere1);
                statement.setObject(2, valueWhere2);
                return executeQuery(statement);
            } else {
                return false;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    /**
     * I made this query strictly private since we need to sanitize the input for using this.
     * <p>
     * This methode executeQuery from a prepared statement.
     *
     * @param statement is a statement that has sanitized input.
     * @return boolean if the execution was a succes.
     */
    private boolean executeQuery(PreparedStatement statement) {
        try (Connection connection = getConnection()) {
            if (connection != null) {
                statement.execute();
                statement.close();
                return true;
            } else {
                return false;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    /**
     * This methode get the object.
     *
     * @param table     the className were it needs to be updated.
     * @param attribute the attribute name to be demanded
     * @param value     the value of that attribute.
     * @return resultSet with the object that have been asked for.
     */
    protected ResultSet getObject(String table, String attribute, Object value) {
        try (Connection connection = getConnection()) {
            if (connection != null) {
                PreparedStatement ps = connection.prepareStatement(String.format(
                        "SELECT * FROM %s WHERE %s = ?", table, attribute));
                ps.setObject(1, value);
                return ps.executeQuery();
            }
        } catch (SQLException e) {
            System.out.println("Failed to execute SQL statement: " + e.getMessage());
        }
        return null;
    }

    /**
     * This methode updates an object in sql with two properties.
     *
     * @param table      the className were it needs to be updated.
     * @param attribute1 the attribute name to be demanded
     * @param value1     the value of that attribute.
     * @param attribute2 the attribute name to be demanded
     * @param value2     the value of that attribute.
     * @return resultSet with the object that have been asked for.
     */
    protected ResultSet getObject(String table, String attribute1, Object value1, String attribute2, Object value2) {
        try (Connection connection = getConnection()) {
            if (connection != null) {
                PreparedStatement ps = connection.prepareStatement(String.format(
                        "SELECT * FROM %s WHERE %s = ? AND %s = ?", table, attribute1, attribute2));
                if (value1 instanceof Date) {
                    ps.setTimestamp(1, new Timestamp(((Date) value1).getTime()));
                } else {
                    ps.setObject(1, value1);
                }
                if (value2 instanceof Date) {
                    ps.setTimestamp(2, new Timestamp(((Date) value2).getTime()));
                } else {
                    ps.setObject(2, value2);
                }
                return ps.executeQuery();
            }
        } catch (SQLException e) {
            System.out.println("Failed to execute SQL statement: " + e.getMessage());
        }
        return null;
    }

    /**
     * This methode gets all attributes from a table
     *
     * @param table is the table from which you get all attributes.
     * @return resultSet with all those attributes or an error if something went wrong.
     */
    protected ResultSet getAll(String table) {
        try (Connection connection = getConnection()) {
            if (connection != null) {
                PreparedStatement ps = connection.prepareStatement(String.format(
                        "SELECT * FROM %s;", table));
                return ps.executeQuery();
            }
        } catch (SQLException e) {
            System.out.println("Failed to execute SQL statement: " + e.getMessage());
        }
        return null;
    }

    /**
     * This methode gets 1 attribute from a table
     *
     * @param table          is the table from which you get all attributes.
     * @param attributeGet   is the attribute you want to get.
     * @param attributeWhere is the attribute that demanding the properties.
     * @param value          is the value of the attribute that has that properties.
     * @return resultSet with all those attributes or an error if something went wrong.
     */
    protected ResultSet getAttribute(String table, String attributeGet, String attributeWhere, Object value) {
        try (Connection connection = getConnection()) {
            if (connection != null) {
                PreparedStatement ps = connection.prepareStatement(String.format(
                        "SELECT %s FROM %S WHERE %s = ?;", attributeGet, table, attributeWhere));
                ps.setObject(1, value);
                return ps.executeQuery();
            }
        } catch (SQLException e) {
            System.out.println("Failed to execute SQL statement: " + e.getMessage());
        }
        return null;
    }

}