package com.oasis.database.connector;

import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;
import com.oasis.database.Connector;
import com.oasis.model.Physician;
import com.oasis.model.PhysicianDesignation;
import com.oasis.model.Telephone;
import com.oasis.services.PhysicianServices;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class PhysicianConnector extends Connector {
    public HashMap<Integer, Physician> getPhysicianHashMap() {
        HashMap<Integer, Physician> physicianHashMap = new HashMap<>();

        try {
            PreparedStatement preparedStatement = (PreparedStatement) getConnection().prepareStatement("SELECT * FROM physician");
            getPhysicianGeneralDetails(preparedStatement, physicianHashMap);

            getPhysicianTelephoneDetails(physicianHashMap);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return physicianHashMap;
    }

    private void getPhysicianGeneralDetails(PreparedStatement preparedStatement, HashMap<Integer, Physician> physicianHashMap) {
        try {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("physician.id");
                String firstName = resultSet.getString("physician.first_name");
                String middleName = resultSet.getString("physician.middle_name");
                String lastName = resultSet.getString("physician.last_name");
                PhysicianDesignation physicianDesignation = PhysicianServices.getPhysicianDesignationById(
                        resultSet.getInt("physician.physician_designation_id"));

                if (!physicianHashMap.containsKey(id)) {
                    Physician physician = new Physician(id, firstName, middleName, lastName, physicianDesignation);
                    physicianHashMap.put(id, physician);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void getPhysicianTelephoneDetails(HashMap<Integer, Physician> physicianHashMap) {
        try {
            PreparedStatement preparedStatement = (PreparedStatement) getConnection().prepareStatement("SELECT * FROM physician_telephone");
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int physicianId = resultSet.getInt("physician_telephone.physician_id");
                int physicianTelephoneId = resultSet.getInt("physician_telephone.id");
                String physicianTelephoneTelephone = resultSet.getString("physician_telephone.telephone");
                Telephone telephone = new Telephone(physicianTelephoneId, physicianTelephoneTelephone);

                physicianHashMap.get(physicianId).getTelephoneArrayList().add(telephone);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            // Expected when all the physicians are not in the hash map
        }
    }

    public HashMap<Integer, PhysicianDesignation> getPhysicianDesignationhashMap() {
        HashMap<Integer, PhysicianDesignation> physicianDesignationHashMap = new HashMap<>();

        try {
            PreparedStatement preparedStatement = (PreparedStatement) getConnection().prepareStatement("SELECT * FROM physician_designation");
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("physician_designation.id");
                String name = resultSet.getString("physician_designation.name");

                PhysicianDesignation physicianDesignation = new PhysicianDesignation(id, name);
                physicianDesignationHashMap.put(id, physicianDesignation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return physicianDesignationHashMap;
    }

    public HashMap<Integer, Telephone> getPhysicianTelephoneHashMap() {
        HashMap<Integer, Telephone> physicianTelephoneHashMap = new HashMap<>();

        try {
            PreparedStatement preparedStatement = (PreparedStatement) getConnection().prepareStatement("SELECT * FROM physician_telephone");
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("physician_telephone.id");
                String telephone = resultSet.getString("physician_telephone.telephone");

                Telephone telephone1 = new Telephone(id, telephone);
                physicianTelephoneHashMap.put(id, telephone1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return physicianTelephoneHashMap;
    }

    public void updatePhysician(Physician physician) {
        try {
            PreparedStatement preparedStatement = (PreparedStatement) getConnection().prepareStatement("UPDATE physician SET " +
                    "first_name = ?, " +
                    "middle_name = ?, " +
                    "last_name = ?, " +
                    "physician_designation_id = ? " +
                    "WHERE id = ?");
            preparedStatement.setString(1, physician.getFirstName());
            preparedStatement.setString(2, physician.getMiddleName());
            preparedStatement.setString(3, physician.getLastName());
            preparedStatement.setInt(4, physician.getPhysicianDesignationObjectProperty().getId());
            preparedStatement.setInt(5, physician.getId());

            preparedStatement.execute();

            for (Telephone telephone : physician.getTelephoneArrayList()) {
                if (telephone.getId() == 0) {
                    newPhysicianTelephone(physician.getId(), telephone);
                } else {
                    updatePhysicianTelephone(telephone);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void newPhysician(Physician physician) {
        try {
            PreparedStatement preparedStatement = (PreparedStatement) getConnection().prepareStatement("INSERT INTO " +
                    "physician(first_name, middle_name, last_name, physician_designation_id) " +
                    "VALUES(?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, physician.getFirstName());
            preparedStatement.setString(2, physician.getMiddleName());
            preparedStatement.setString(3, physician.getLastName());
            preparedStatement.setInt(4, physician.getPhysicianDesignationObjectProperty().getId());

            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                physician.setId(resultSet.getInt(1));
            }

            for (Telephone telephone : physician.getTelephoneArrayList()) {
                newPhysicianTelephone(physician.getId(), telephone);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deletePhysician(Physician physician) {
        try {
            PreparedStatement preparedStatement = (PreparedStatement) getConnection().prepareStatement("DELETE FROM physician " +
                    "WHERE id = ?");
            preparedStatement.setInt(1, physician.getId());

            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void newPhysicianTelephone(int physiciaid, Telephone telephone) throws SQLException {
        PreparedStatement preparedStatement = (PreparedStatement) getConnection().prepareStatement("INSERT INTO " +
                "physician_telephone(physician_id, telephone) " +
                "VALUES(?, ?)");
        preparedStatement.setInt(1, physiciaid);
        preparedStatement.setString(2, telephone.getTelephone());

        preparedStatement.execute();
    }

    private void updatePhysicianTelephone(Telephone telephone) throws SQLException {
        PreparedStatement preparedStatement = (PreparedStatement) getConnection().prepareStatement("UPDATE physician_telephone SET " +
                "telephone = ? " +
                "WHERE id = ?");

        preparedStatement.setString(1, telephone.getTelephone());
        preparedStatement.setInt(2, telephone.getId());

        preparedStatement.execute();
    }

    public HashMap<Integer, Physician> getPhysicianLike(String param) {
        HashMap<Integer, Physician> physicianHashMap = new HashMap<>();

        try {
            PreparedStatement preparedStatement = (PreparedStatement) getConnection().prepareStatement("SELECT * FROM physician " +
                    "LEFT JOIN physician_telephone ON physician.id = physician_telephone.physician_id " +
                    "WHERE CONCAT_WS(' ', first_name, middle_name, last_name) LIKE '%" + param + "%'");
            getPhysicianGeneralDetails(preparedStatement, physicianHashMap);

            getPhysicianTelephoneDetails(physicianHashMap);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return physicianHashMap;
    }
}
