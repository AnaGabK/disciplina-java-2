package br.edu.utfpr.validacao;

public record Violacao(String campo, String mensagem) {

    public static final String DATA_NASCIMENTO_CAMPO = "dataNascimento";
    public static final String CAPITAL_SOCIAL_CAMPO = "capitalSocial";

    public static final String DATA_NASCIMENTO_MENSAGEM = "deve ser maior que 18 anos";
    public static final String CAPITAL_SOCIAL_MENSAGEM = "deve ser maior que R$ 10.000,00";

    @Override
    public String toString() {
        return "campo '" + campo + "': " + mensagem;
    }
}
