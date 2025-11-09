package view.users;

import java.sql.SQLException;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import model.User;
import view.listeners.EventListerner;
import controllers.users.UserController;
import controllers.users.listeners.MailEvent;
import controllers.users.listeners.UserListener;

public class JTableList extends JTable implements UserListener, EventListerner {

	private static final long serialVersionUID = 1L;

	private TableModel model = new TableModel();

	public JTableList() {
		this.setModel(model);
		this.getTableHeader().setReorderingAllowed(false);
		UserController.getInstance().addUserListener(this);
		loadUsers();
	}

	public void loadUsers() {
		try {
			for (User user : UserController.getInstance().allUsers()) {
				model.insertRow(0, user.toArray());
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Erro", e.getMessage(), JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	private class TableModel extends DefaultTableModel {

		private static final long serialVersionUID = 1L;

		public TableModel() {
			super(new Object[][] {}, new String[] { "id", "Nome", "login" });
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}

	}

	@Override
	public void useradd(MailEvent<User> event) {
		model.insertRow(0, event.getSource().toArray());
	}


    /**
     * NOVO MÉTODO: Ouve o evento de atualização do UserController
     * e atualiza a linha na tabela.
     */
    @Override
    public void userupdated(MailEvent<User> event) {
        User updatedUser = event.getSource();
        Long userId = updatedUser.getId();

        for (int i = 0; i < model.getRowCount(); i++) {
            Long idInTable = Long.parseLong((String) model.getValueAt(i, 0));

            if (idInTable.equals(userId)) {
                model.setValueAt(updatedUser.getName(), i, 1);
                model.setValueAt(updatedUser.getLogin(), i, 2);
                return; // Para o loop
            }
        }
    }


    /**
     * MODIFICADO: Implementa a lógica do botão Editar.
     */
    @Override
    public void cmdEdit() {
        int row = this.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione um usuário para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Pega os dados da linha selecionada
        Long userId = Long.parseLong((String) this.getValueAt(row, 0));
        String name = (String) this.getValueAt(row, 1);
        String login = (String) this.getValueAt(row, 2);

        // Cria o objeto User para enviar ao formulário
        User userToEdit = new User();
        userToEdit.setId(userId);
        userToEdit.setName(name);
        userToEdit.setLogin(login);

        // Abre o formulário NO MODO DE EDIÇÃO
        // (Você precisará criar este método 'showFormForEdit' no seu Form.java)
        Form.showFormForEdit(userToEdit);
    }


	@Override
	public void cmdRemove() {
		if (this.getSelectedRow() != -1) {
			int row = this.getSelectedRow();
			Long userId = Long.parseLong((String) this.getValueAt(row, 0));
			try {
				UserController.getInstance().remove(userId);
				model.removeRow(row);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void cmdDetails() {
		System.out.println(this.getSelectedRow());
	}

	@Override
	public void cmdAdd() {
		Form.toggle();
	}

}
