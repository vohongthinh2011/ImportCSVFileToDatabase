package studentmvc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Component;

import studentmvc.model.User;
import studentmvc.utils.CommonUtils;

@Component
public class UserDaoImpl implements UserDao {
	NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	public void setNamedParameterJdbcTemplate(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
	}

	public void process(List<String> filesPath) throws JAXBException {
		// TODO Auto-generated method stub
		List<User> list = new ArrayList<User>();
		// Read Data
		for (String filePath : filesPath) {
			if (CommonUtils.getFileExtension(filePath).equals("csv")) {
				// Read csv file
				list.addAll(CommonUtils.readCsv(filePath));
			} else if (CommonUtils.getFileExtension(filePath).equals("xml")) {
				list.addAll(CommonUtils.readXml(filePath));
			}
		}
		// Import Data
		importData(list);
	}
	
	public void importData(List<User> list) {
		String sql = "INSERT INTO student(schoolYear,campus,studentId,entryDate,gradeLevel,name) VALUES (:schoolYear,:campus,:studentId,:entryDate,:gradeLevel,:name)";
		SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(list.toArray());
		namedParameterJdbcTemplate.batchUpdate(sql, batch);
	}
}