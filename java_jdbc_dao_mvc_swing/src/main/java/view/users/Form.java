package view.users;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import controllers.users.UserController;
import model.User;

public class Form extends JDialog{

	private static final long serialVersionUID = 1L;
	
	private static Form form = new Form();

    private User userToEdit = null;

	private JTextField jtfName;
	private JTextField jtfLogin;

	private JButton jbSave;
	private JButton jbCancel;

	public Form() {
		createForms();
		createButtons();
		registerListeners();
		configure();
	}	

	private void configure(){
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.pack();
		this.setLocationRelativeTo(this.getRootPane());
        this.setTitle("Formulário de Usuário");
	}
	
	private void createForms(){
		JPanel jpForm = new JPanel(new GridLayout(2, 1, 0, 5));

		jpForm.setBorder(BorderFactory.createTitledBorder("Dados Pessoais"));

		jpForm.add(fieldset(new JLabel("Nome: "),
				            jtfName = new JTextField(30)));

		jpForm.add(fieldset(new JLabel("Login: "),
							jtfLogin = new JTextField(30)));

		this.add(jpForm, BorderLayout.CENTER);
	}

	private JPanel fieldset(JComponent...components){
		JPanel fieldset = new JPanel();
		for (JComponent component : components) {
			fieldset.add(component);
		}
		return fieldset;
	}
	
	private void createButtons(){
		JPanel jpButtons = new JPanel();    

		jpButtons.add(jbSave = new JButton("Salvar"));
		jpButtons.add(jbCancel = new JButton("Cancelar"));	

		this.add(jpButtons, BorderLayout.SOUTH);		
	}
	
	private void registerListeners() {
		jbSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				cmdSave();
			}
		});
		jbCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				cmdCancel();
			}
		});
	}

    /**
     * MODIFICADO: Agora este método sabe a diferença entre
     * criar um novo usuário e atualizar um existente.
     */
    private void cmdSave(){
        try {
            User user;

            if (userToEdit == null) {
                user = new User(jtfName.getText(), jtfLogin.getText());
            } else {
                user = userToEdit;
                user.setName(jtfName.getText());
                user.setLogin(jtfLogin.getText());
            }
            UserController.getInstance().save(user);

            JOptionPane.showMessageDialog(this, "Usuário Salvo Com Sucesso", "", JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            //e.printStackTrace();
        }
    }
	
	private void cmdCancel(){
		dispose();
	}
	
	private void clearForm(JTextComponent... jtcomponets){
		for (JTextComponent component : jtcomponets) {
			component.setText("");
		}
	}

    /**
     * MODIFICADO: Limpa o formulário e também reseta o
     * 'userToEdit', para que o formulário esteja pronto para a próxima vez.
     */
    @Override
    public void dispose(){
        super.dispose();
        clearForm(jtfName, jtfLogin);
        this.userToEdit = null; // Reseta o modo de edição
    }

    /**
     * MODIFICADO: Este método (para "Adicionar") agora garante
     * que o formulário está em modo de criação (userToEdit = null).
     */
    public static void toggle(){
        form.setUserToEdit(null); // Garante que está no modo "Add"
        form.setVisible(!form.isVisible());
    }

    /**
     * NOVO MÉTODO: Chamado pela JTableList para abrir
     * o formulário no modo de edição.
     */
    public static void showFormForEdit(User user) {
        form.setUserToEdit(user); // Preenche o formulário
        form.setVisible(true); // Mostra o formulário
    }

    /**
     * NOVO MÉTODO (privado): Preenche os campos do formulário
     * com os dados do usuário a ser editado.
     */
    private void setUserToEdit(User user) {
        this.userToEdit = user;

        if (user != null) {
            // Modo Edição: Preenche os campos
            jtfName.setText(user.getName());
            jtfLogin.setText(user.getLogin());
        } else {
            // Modo Adição: Limpa os campos (redundante com o dispose, mas seguro)
            clearForm(jtfName, jtfLogin);
        }
    }
}