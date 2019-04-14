package rpc;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;

@WebServlet("/register")
public class Register extends HttpServlet{
	private static final long serialVersionUID = 1L;
	
	public Register() {
		super();
	}
	
	
	protected void doPost(HttpServletRequest request, HttpServletResponse
			response) throws ServletException {
		DBConnection connection = DBConnectionFactory.getConnection();
		try {
			JSONObject input = RPCHelper.readJSONObject(request);
			String userId = input.getString("user_id");
			String password = input.getString("password");
			String firstName = input.getString("first_name");
			String lastNameString = input.getString("last_name");
			
			JSONObject object = new JSONObject();
			if(connection.registerUser(userId, password, firstName, lastNameString)) {
				object.put("status", "ok");
			} else {
				object.put("status", "user already exists");
			}
			RPCHelper.writeJsonObject(response, object);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			connection.close();
		}
	}
}
