/*
==========================================================
- Domínio: Biblioteca de Jogos da Steam(reutilizando código anterior);
- Classes de domínio: Jogo (abstrata), JogoPago, JogoFreeToPlay e Biblioteca.
- Classes da tela: JanelaBiblioteca e OuvinteAdicionar.
- Classe de apoio aos padrões: FabricaJogo.

- Dados corretor e incorretos de acordo com as regars:
    Título e gênero não vazios      -> Jogo               -> IllegalArgumentException
    Preço maior ou igual a zero     -> JogoPago           -> IllegalArgumentException
    Desconto entre 0 e 100          -> JogoPago           -> IllegalArgumentException
    Gasto inicial maior ou igual a 0-> JogoFreeToPlay     -> IllegalArgumentException
    Horas jogadas maiores ou iguais a 0 -> Jogo           -> IllegalArgumentException
    Tipo de jogo desconhecido       -> FabricaJogo        -> IllegalArgumentException
    Jogo repetido na biblioteca     -> Biblioteca         -> IllegalArgumentException
    Texto no lugar de número        -> conversão no ouvinte -> NumberFormatException

- Padrões:
    FACTORY METHOD: o formulário mostra para o usuário se o game é pago ou gratuito e
    ele deve decidir qual é, assim, transformando em objeto.
    SINGLETON: a biblioteca precisa ter os jogos, e não ser um jogo, ela enxergada por qualquer tela.
=========================================================
*/

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Main{

	public static void main(String[] args){
		//A JANELA É CRIADA NA THREAD DE EVENTOS DO SWING;
			SwingUtilities.invokeLater(() -> new JanelaBiblioteca().setVisible(true));
	}
}//FIM CLASS MAIN;

/*
==================================================
CLASSES DE DOMÍNIO
==================================================
*/

//SUPERCLASSE ABSTRATA: O QUE É COMUM EM TODO JOGO;
abstract class Jogo{
	//ATRIBUTOS;
		private String titulo;
		private String genero;
		private int horasJogadas;

	//CONSTRUTOR;
		public Jogo(String titulo, String genero){
			setTitulo(titulo);
			setGenero(genero);
			setHorasJogadas(0);
		}

	//GETTERS - LEITURA CONTROLADA;
		public String getTitulo(){
			return titulo;
		}

		public String getGenero(){
			return genero;
		}

		public int getHorasJogadas(){
			return horasJogadas;
		}

	//SETTERS - ESCRITA PROTEGIDA;
		public void setTitulo(String t){
			if (t == null || t.trim().isEmpty()){
				throw new IllegalArgumentException("O título do jogo não pode ser vazio.");
			}
			titulo = t.trim();
		}

		public void setGenero(String g){
			if (g == null || g.trim().isEmpty()){
				throw new IllegalArgumentException("O gênero do jogo não pode ser vazio.");
			}
			genero = g.trim();
		}

		public void setHorasJogadas(int h){
			if (h < 0){
				throw new IllegalArgumentException("As horas jogadas não podem ser negativas.");
			}
			horasJogadas = h;
		}

	//CADA TIPO DE JOGO CALCULA O CUSTO DO SEU JEITO;
		public abstract double calcularCustoTotal();

	//EXIBE DADOS DO OBJETO;
		public String info(){
			return titulo + " | " + genero + " | Horas jogadas: " + horasJogadas;
		}
}//FIM CLASS JOGO;

//SUBCLASSE 1: JOGO COMPRADO, COM PREÇO E DESCONTO;
class JogoPago extends Jogo{
	//ATRIBUTOS PRÓPRIOS;
		private double preco;
		private int desconto; //PERCENTUAL DE 0 A 100;

	//CONSTRUTOR;
		public JogoPago(String titulo, String genero, double preco, int desconto){
			super(titulo, genero);
			setPreco(preco);
			setDesconto(desconto);
		}

	//GETTERS;
		public double getPreco(){
			return preco;
		}

		public int getDesconto(){
			return desconto;
		}

	//SETTERS - ESCRITA PROTEGIDA;
		public void setPreco(double p){
			if (!Double.isFinite(p) || p < 0){
				throw new IllegalArgumentException("O preço deve ser um número maior ou igual a zero.");
			}
			preco = p;
		}

		public void setDesconto(int d){
			if (d < 0 || d > 100){
				throw new IllegalArgumentException("O desconto deve estar entre 0 e 100 (%).");
			}
			desconto = d;
		}

	//IMPLEMENTAÇÃO PRÓPRIA DO MÉTODO ABSTRATO: PREÇO MENOS O DESCONTO;
		@Override
		public double calcularCustoTotal(){
			return preco * (100 - desconto) / 100.0;
		}

	//SOBRESCRITA QUE REAPROVEITA A VERSÃO HERDADA;
		@Override
		public String info(){
			return super.info() + " | Pago: R$ " + String.format("%.2f", preco) + " (desconto " + desconto + "%)";
		}
}//FIM CLASS JOGOPAGO;

//SUBCLASSE 2: JOGO GRATUITO, O CUSTO VEM DAS MICROTRANSAÇÕES;
class JogoFreeToPlay extends Jogo{
	//ATRIBUTO PRÓPRIO;
		private double gastoMicrotransacoes;

	//CONSTRUTOR;
		public JogoFreeToPlay(String titulo, String genero, double gastoInicial){
			super(titulo, genero);
			setGastoMicrotransacoes(gastoInicial);
		}

	//GETTER;
		public double getGastoMicrotransacoes(){
			return gastoMicrotransacoes;
		}

	//SETTER - ESCRITA PROTEGIDA;
		public void setGastoMicrotransacoes(double g){
			if (!Double.isFinite(g) || g < 0){
				throw new IllegalArgumentException("O gasto em microtransações deve ser um número maior ou igual a zero.");
			}
			gastoMicrotransacoes = g;
		}

	//IMPLEMENTAÇÃO PRÓPRIA DO MÉTODO ABSTRATO: O QUE FOI GASTO DENTRO DO JOGO;
		@Override
		public double calcularCustoTotal(){
			return gastoMicrotransacoes;
		}

	//SOBRESCRITA QUE REAPROVEITA A VERSÃO HERDADA;
		@Override
		public String info(){
			return super.info() + " | Free to Play, gasto em microtransações: R$ " + String.format("%.2f", gastoMicrotransacoes);
		}
}//FIM CLASS JOGOFREETOPLAY;

/*
==================================================
PADRÃO 1: FACTORY METHOD (família criacional)
==================================================
- NOME: Factory Method

- PROBLEMA: o formulário entrega só duas opções de jogos e alguém
  precisa transformá-lo no objeto certo. Se esse if/else ficasse dentro do ouvinte
  da tela, ele se repetiria em todo lugar que cria jogos (importação, save, etc.).

- SOLUÇÃO: FabricaJogo.criar concentra a decisão e devolve sempre o tipo
  genérico Jogo. A tela só conhece Jogo e FabricaJogo, nunca JogoPago nem
  JogoFreeToPlay.

- CONSEQUÊNCIAS NO MEU CÓDIGO:
    (+) O polimorfismo segue valendo: a tela e a Biblioteca tratam tudo como Jogo.
    (-) Uma classe a mais no projeto.
    (-) criar() recebe o parâmetro "desconto", que só o JogoPago usa (o
        JogoFreeToPlay ignora). Essa assinatura única é o preço de a tela não
        precisar saber o tipo.
==================================================
*/
class FabricaJogo{
	public static final String TIPO_PAGO = "Pago";
	public static final String TIPO_FREE = "Free to Play";
	public static final String[] TIPOS = {TIPO_PAGO, TIPO_FREE};

	//O PARÂMETRO "VALOR" É O PREÇO (PAGO) OU O GASTO INICIAL (FREE TO PLAY);
	public static Jogo criar(String tipo, String titulo, String genero, double valor, int desconto){
		if (TIPO_PAGO.equals(tipo)){
			return new JogoPago(titulo, genero, valor, desconto);
		}
		if (TIPO_FREE.equals(tipo)){
			return new JogoFreeToPlay(titulo, genero, valor);
		}
		throw new IllegalArgumentException("Tipo de jogo desconhecido: " + tipo);
	}
}//FIM CLASS FABRICAJOGO;

/*
==================================================
PADRÃO 2: SINGLETON (família criacional)
==================================================
- NOME: Singleton.

- PROBLEMA: a biblioteca do usuário precisa ser única, e qualquer tela deve
  enxergar os mesmos jogos. Com "new Biblioteca()" em cada tela, cada janela teria
  uma lista diferente.

- SOLUÇÃO: construtor privado, um atributo estático que guarda a instância e o
  método getInstance(), que cria o objeto na primeira chamada e devolve sempre o mesmo.

- CONSEQUÊNCIAS NO MEU CÓDIGO:
    (+) A unicidade é garantida pela classe: "new Biblioteca()" fora dela nem compila.
    (-) É um acesso global disfarçado: a JanelaBiblioteca chama
        Biblioteca.getInstance() direto, e essa dependência não aparece no
        construtor da janela.
    (-) O estado sobrevive até o programa acabar e não há como zerar a lista, o que
        dificultaria testes automáticos.
==================================================
*/
class Biblioteca{
	//A CLASSE GUARDA A ÚNICA INSTÂNCIA;
		private static Biblioteca instancia;

	//COMPOSIÇÃO: A BIBLIOTECA TEM JOGOS;
		private final ArrayList<Jogo> jogos = new ArrayList<>();

	//CONSTRUTOR PRIVADO: IMPEDE O NEW DE FORA;
		private Biblioteca(){ }

		public static Biblioteca getInstance(){
			if (instancia == null){
				instancia = new Biblioteca();
			}
			return instancia;
		}

	//REGRA DE DOMÍNIO: NÃO ACEITA JOGO NULO NEM TÍTULO REPETIDO;
		public void adicionar(Jogo novo){
			if (novo == null){
				throw new IllegalArgumentException("Não é possível guardar um jogo nulo.");
			}
			for (Jogo existente : jogos){
				if (existente.getTitulo().equalsIgnoreCase(novo.getTitulo())){
					throw new IllegalArgumentException("O jogo \"" + novo.getTitulo() + "\" já está na biblioteca.");
				}
			}
			jogos.add(novo);
		}

	//LISTA SOMENTE PARA LEITURA: QUEM CONSULTA NÃO MEXE NA COLEÇÃO;
		public List<Jogo> getJogos(){
			return Collections.unmodifiableList(jogos);
		}

	//LAÇO POLIMÓRFICO, SEM INSTANCEOF;
		public double getTotalInvestido(){
			double total = 0;
			for (Jogo j : jogos){
				total += j.calcularCustoTotal();
			}
			return total;
		}
}//FIM CLASS BIBLIOTECA;

/*
==================================================
CLASSES DA TELA
==================================================
*/
class JanelaBiblioteca extends JFrame{
	//COMPONENTES;
		private final JTextField campoTitulo = new JTextField(20);
		private final JTextField campoGenero = new JTextField(20);
		private final JComboBox<String> seletorTipo = new JComboBox<>(FabricaJogo.TIPOS);
		private final JTextField campoValor = new JTextField(20);
		private final JTextField campoDesconto = new JTextField(20);
		private final JTextField campoHoras = new JTextField(20);
		private final JButton botaoAdicionar = new JButton("Adicionar à biblioteca");
		private final JTextArea areaLista = new JTextArea(10, 50);
		private final JLabel rotuloTotal = new JLabel();

	//CONSTRUTOR: MONTA A JANELA SÓ COM GERENCIADORES DE LAYOUT, SEM COORDENADAS FIXAS;
		public JanelaBiblioteca(){
			super("Biblioteca Steam");
			setDefaultCloseOperation(EXIT_ON_CLOSE);

			JPanel conteudo = (JPanel) getContentPane();
			conteudo.setLayout(new BorderLayout(10, 10));
			conteudo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

			areaLista.setEditable(false);
			areaLista.setLineWrap(true);
			areaLista.setWrapStyleWord(true);

			conteudo.add(montarFormulario(), BorderLayout.NORTH);
			conteudo.add(new JScrollPane(areaLista), BorderLayout.CENTER);
			conteudo.add(rotuloTotal, BorderLayout.SOUTH);

			botaoAdicionar.addActionListener(new OuvinteAdicionar());

			atualizarLista();
			pack();
			setLocationRelativeTo(null);
		}

	//FORMULÁRIO EM GRADE (GRIDBAGLAYOUT): RÓTULO NA COLUNA 0, CAMPO NA COLUNA 1;
		private JPanel montarFormulario(){
			JPanel painel = new JPanel(new GridBagLayout());

			adicionarLinha(painel, 0, "Título:", campoTitulo);
			adicionarLinha(painel, 1, "Gênero:", campoGenero);
			adicionarLinha(painel, 2, "Tipo:", seletorTipo);
			adicionarLinha(painel, 3, "Valor:", campoValor);
			adicionarLinha(painel, 4, "Desconto:", campoDesconto);
			adicionarLinha(painel, 5, "Horas jogadas:", campoHoras);

			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 6;
			c.gridwidth = 2;
			c.anchor = GridBagConstraints.EAST;
			c.insets = new Insets(8, 4, 4, 4);
			painel.add(botaoAdicionar, c);

			return painel;
		}

		private void adicionarLinha(JPanel painel, int linha, String texto, java.awt.Component campo){
			GridBagConstraints c = new GridBagConstraints();
			c.gridy = linha;
			c.insets = new Insets(4, 4, 4, 4);

			c.gridx = 0;
			c.anchor = GridBagConstraints.WEST;
			painel.add(new JLabel(texto), c);

			c.gridx = 1;
			c.weightx = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			painel.add(campo, c);
		}

	//REDESENHA A LISTA E O TOTAL A PARTIR DA BIBLIOTECA ÚNICA;
		private void atualizarLista(){
			StringBuilder texto = new StringBuilder();
			Biblioteca biblioteca = Biblioteca.getInstance();
			for (Jogo j : biblioteca.getJogos()){
				texto.append(j.info())
					.append(" -> Custo total: ")
					.append(String.format("%.2f", j.calcularCustoTotal()))
					.append("\n");
			}
			areaLista.setText(texto.toString());
			rotuloTotal.setText(String.format("Jogos: %d | Total investido: R$ %.2f",
					biblioteca.getJogos().size(), biblioteca.getTotalInvestido()));
		}

		private void limparCampos(){
			campoTitulo.setText("");
			campoGenero.setText("");
			campoValor.setText("");
			campoDesconto.setText("");
			campoHoras.setText("");
			campoTitulo.requestFocusInWindow();
		}

	//CONVERSÃO DE TEXTO PARA NÚMERO (CAMPO VAZIO VALE ZERO); TEXTO INVÁLIDO LANÇA NUMBERFORMATEXCEPTION;
		private double lerDecimal(String texto){
			String limpo = texto.trim().replace(',', '.');
			if (limpo.isEmpty()){
				return 0;
			}
			return Double.parseDouble(limpo);
		}

		private int lerInteiro(String texto){
			String limpo = texto.trim();
			if (limpo.isEmpty()){
				return 0;
			}
			return Integer.parseInt(limpo);
		}

	//OUVINTE DO BOTÃO: LÊ OS CAMPOS, CHAMA O DOMÍNIO E TRADUZ A EXCEÇÃO EM DIÁLOGO;
		private class OuvinteAdicionar implements ActionListener{
			@Override
			public void actionPerformed(ActionEvent e){
				try {
					String tipo = (String) seletorTipo.getSelectedItem();
					double valor = lerDecimal(campoValor.getText());
					int desconto = lerInteiro(campoDesconto.getText());
					int horas = lerInteiro(campoHoras.getText());

					//FACTORY METHOD: A TELA PEDE O JOGO PRONTO, SEM SABER A SUBCLASSE;
					Jogo jogo = FabricaJogo.criar(tipo, campoTitulo.getText(), campoGenero.getText(), valor, desconto);
					jogo.setHorasJogadas(horas);

					//SINGLETON: TODA TELA GUARDA NA MESMA BIBLIOTECA;
					Biblioteca.getInstance().adicionar(jogo);

					atualizarLista();
					limparCampos();
				} catch (NumberFormatException ex){
					//ESTE CATCH VEM ANTES PORQUE NUMBERFORMATEXCEPTION É UMA ILLEGALARGUMENTEXCEPTION;
					JOptionPane.showMessageDialog(JanelaBiblioteca.this,
							"Digite apenas números em valor, desconto e horas.",
							"Número inválido", JOptionPane.ERROR_MESSAGE);
				} catch (IllegalArgumentException ex){
					//A MENSAGEM VEM PRONTA DO DOMÍNIO: A TELA SÓ A EXIBE;
					JOptionPane.showMessageDialog(JanelaBiblioteca.this,
							ex.getMessage(),
							"Dado inválido", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
}//FIM CLASS JANELABIBLIOTECA;

/*
==================================================
AUTOAVALIAÇÃO (10 minutos)
==================================================
- Consegui realizar a adaptação do meu código antigo para usar o swing,
foi bem difícil a implementação, visto que eu não compreendi muito bem como
colocar corretamente algumas funções, mas o principal, foi na execução da janela,
nela eu pedi para a IA fazer por si só, e depois me explicar as etapas, visto que
eu estava fazendo tudo errado;
*/