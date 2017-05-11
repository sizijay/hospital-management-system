package com.oasis.database.connector;

import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;
import com.oasis.database.Connect;
import com.oasis.model.Physician;
import com.oasis.model.PhysicianDesignation;
import com.oasis.model.PhysicianTelephone;
import com.oasis.services.PhysicianDesignationServices;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class PhysicianConnector extends Connect {
    public HashMap<Integer, Physician> getPhysicianHashMap() {
        HashMap<Integer, Physician> physicianHashMap = new HashMap<>();

        try {
            PreparedStatement preparedStatement = (PreparedStatement) getConnection().prepareStatement("SELECT * FROM physician " +
                    "LEFT JOIN physician_telephone ON physician.id = physician_telephone.physician_id");
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("physician.id");
                String firstName = resultSet.getString("physician.first_name");
                String middleName = resultSet.getString("physician.middle_name");
                String lastName = resultSet.getString("physician.last_name");
                PhysicianDesignation physicianDesignation =
                        PhysicianDesignationServices.getPhysicianDesignationById(
                                resultSet.getInt("physician.physician_designation_id"));
                int physicianTelephoneID = resultSet.getInt("physician_telephone.id");
                String physicianTelephoneTelephone = resultSet.getString("physician_telephone.telephone");

                if (!physicianHashMap.containsKey(id)) {
                    Physician physician = new Physician(id, firstName, middleName, lastName, physicianDesignation);
                    physicianHashMap.put(id, physician);
                }

                PhysicianTelephone physicianTelephone = new PhysicianTelephone(physicianTelephoneID, physicianTelephoneTelephone);
                physicianHashMap.get(id).getPhysicianTelephoneArrayList().add(physicianTelephone);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return physicianHashMap;
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

            for (PhysicianTelephone physicianTelephone : physician.getPhysicianTelephoneArrayList()) {
                if(physicianTelephone.getId() == 0){
                    newPhysicianTelephone(physician.getId(), physicianTelephone);
                }else {
                    updatePhysicianTelephone(physicianTelephone);
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
            if(resultSet.next()) {
                physician.setId(resultSet.getInt(1));
            }

            for (PhysicianTelephone physicianTelephone : physician.getPhysicianTelephoneArrayList()) {
                newPhysicianTelephone(physician.getId(), physicianTelephone);
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

    private void newPhysicianTelephone(int physicianId, PhysicianTelephone physicianTelephone) throws SQLException {
        PreparedStatement preparedStatement1 = (PreparedStatement) getConnection().prepareStatement("INSERT INTO " +
                "physician_telephone(physician_id, telephone) " +
                "VALUES(?, ?, ?)");
        preparedStatement1.setInt(1, physicianId);
        preparedStatement1.setString(2, physicianTelephone.getTelephone());

        preparedStatement1.execute();
    }

    private void updatePhysicianTelephone(PhysicianTelephone physicianTelephone) throws SQLException {
        PreparedStatement preparedStatement = (PreparedStatement) getConnection().prepareStatement("UPDATE physician_telephone SET " +
                "telephone = ? " +
                "WHERE id = ?");

        preparedStatement.setString(1, physicianTelephone.getTelephone());
        preparedStatement.setInt(2, physicianTelephone.getId());

        preparedStatement.execute();
    }
}
