package br.com.alura.bytebank.domain.conta;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import br.com.alura.bytebank.domain.RegraDeNegocioException;
import br.com.alura.bytebank.domain.cliente.Cliente;
import br.com.alura.bytebank.domain.cliente.DadosCadastroCliente;

public class ContaDAO {
    private Connection conn;
    ContaDAO(Connection connection){

        this.conn = connection;
    }

    public void salvar(DadosAberturaConta dadosDaConta){
        var cliente = new Cliente(dadosDaConta.dadosCliente());
        var conta = new Conta(dadosDaConta.numero(), cliente, BigDecimal.ZERO, true);

         String sql = "INSERT INTO conta (numero, saldo, cliente_nome, cliente_cpf, cliente_email)" +
        "VALUES (?, ?, ?, ?, ?)";

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, conta.getNumero());
            preparedStatement.setBigDecimal(2, BigDecimal.ZERO);
            preparedStatement.setString(3,cliente.getNome());
            preparedStatement.setString(4,cliente.getCpf());
            preparedStatement.setString(5, cliente.getEmail());
            preparedStatement.setBoolean(6, conta.getEstaAtiva());
            preparedStatement.execute();
            preparedStatement.close();
            conn.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Conta gerarConta(ResultSet resultSet) throws SQLException {
        var dados = new DadosCadastroCliente(
            resultSet.getString("cliente_nome"),
            resultSet.getString("cliente_cpf"),
            resultSet.getString("cliente_email")
        );
        var cliente = new Cliente(dados);
        return new Conta(
            resultSet.getInt("numero"),
            cliente,
            resultSet.getBigDecimal("saldo"),
            resultSet.getBoolean("esta_ativa")
        );
    }

    public Set<Conta> listar(){
        Set<Conta> contas = new HashSet<>();
        String sql = "SELECT * FROM conta WHERE esta_ativa = true";
       
        
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            
            while (resultSet.next()) {
                contas.add(gerarConta(resultSet));
            }
            preparedStatement.close();
            return contas;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Conta listarPorNumero(Integer numero){
         String sql = "SELECT * FROM conta WHERE numero = ? AND esta_ativa = true";
        
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, numero);
            var resultSet = preparedStatement.executeQuery();
            
            if (!resultSet.next()) {
                throw new RegraDeNegocioException("Não existe conta cadastrada com esse número!");
            }
            
            Conta conta = gerarConta(resultSet);
            preparedStatement.close();
            return conta;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void alterar(Integer numero, BigDecimal valor) {
        PreparedStatement ps;
        String sql = "UPDATE conta SET saldo = ? WHERE numero = ?";

        try {
            conn.setAutoCommit(false);

            ps = conn.prepareStatement(sql);

            ps.setBigDecimal(1, valor);
            ps.setInt(2, numero);

            ps.execute();
            conn.commit();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
    }

    public void alterarLogico(Integer numero) {
        PreparedStatement ps;
        String sql = "UPDATE conta SET esta_ativa = false WHERE numero = ?";

        try {
            conn.setAutoCommit(false);

            ps = conn.prepareStatement(sql);

            ps.setInt(1, numero);

            ps.execute();
            conn.commit();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
    }


    public void encerrar(Integer numero) {
        String sql = "DELETE FROM conta WHERE numero = ?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, numero);
            preparedStatement.execute();
            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
         throw new   RuntimeException(e);
        }
    }
}
