package controllers.users;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import controllers.users.listeners.MailEvent;
import controllers.users.listeners.UserListener;
import model.User;

public class UserController {
	
	private List<UserListener> userListeners = new ArrayList<UserListener>();
	
	private static UserController instance = new UserController();
	
	private UserController(){}
	
	public static UserController getInstance() {
		return instance;
	}

    public User save(User user) throws SQLException {
        if (user != null) {
            // 1. Verifica se é um usuário novo ANTES de salvar
            boolean isNewUser = (user.getId() == null);

            // 2. Chama o método save() inteligente do Model
            user.save();

            // 3. Notifica o listener correto
            if (isNewUser) {
                notifyUserAdded(user);
            } else {
                notifyUserUpdated(user);
            }
        }
        return user;
    }
	
	public void remove(Long userId) throws SQLException{
		User user = User.findById(userId);
		user.delete();
	}
	
	public List<User> allUsers() throws SQLException{
		return User.all();
	}
	
	public synchronized void addUserListener(UserListener l) {
		if(!userListeners.contains(l)) {
			userListeners.add(l);
		}
	}

	private void notifyUserAdded(User user) {
		MailEvent<User> event = new MailEvent<User>(user);
		for (UserListener listener : userListeners) {
			listener.useradd(event);
		}
	}

    /**
     * NOVO MÉTODO: Notifica os listeners sobre ATUALIZAÇÃO
     */
    private void notifyUserUpdated(User user) {
        MailEvent<User> event = new MailEvent<User>(user);
        for (UserListener listener : userListeners) {
            listener.userupdated(event);
        }
    }

}
