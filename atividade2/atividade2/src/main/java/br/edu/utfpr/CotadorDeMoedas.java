package br.edu.utfpr;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

public class CotadorDeMoedas {


    public Path verificarEntrada() {
        // TODO
        return Path.of("entrada", "moedas.txt");
    }

    public Path verificarSaida() {
        // TODO
        Path dir = Path.of("saida", "cotacoes.csv");
        try{
            Files.createDirectories(dir.getParent());
            return dir;
        }catch(IOException e){
            IO.println("Erro ao criar diretório de saída.");
            System.exit(1);
        }

        return Path.of("");
    }

    public List<String> lerArquivoDeMoedas(Path entrada) {
        // TODO
        if (!Files.exists(entrada)) {
            return List.of();
        }

        try(Stream<String> linhas = Files.lines(entrada)){
            return linhas.map(String::trim)
                    .filter(s -> !s.isBlank())
                    .map(String::toUpperCase)
                    .filter(s -> s.matches("[A-Z]{3}"))
                    .toList();
        }catch(IOException e){
            return List.of();
        }
    }

    public void cotarERegistrar(Path saida, List<String> moedas, ClienteCambio cliente) {
        // TODO
        if (!moedas.isEmpty()) {
            List<CompletableFuture<Optional<Cotacao>>> futuros =
                    moedas.stream()
                            .map(cliente::consultar)
                            .toList();

            CompletableFuture
                    .allOf(futuros.toArray(new CompletableFuture[0]))
                    .join();

            List<Cotacao> cotacoes = futuros.stream()
                    .map(CompletableFuture::join)      // Optional<Cotacao>
                    .flatMap(Optional::stream)         // Cotacao
                    .toList();

            gravarEmCSV(saida, cotacoes);
        }
    }

    private void gravarEmCSV(Path saida, List<Cotacao> cotacoes) {
        // TODO
//        try (BufferedWriter writer = Files.newBufferedWriter(saida, CREATE, APPEND)) {
//            String conteudo = "moeda,valor,coletadoEm\n" +
//                    cotacoes.stream()
//                            .map(Cotacao::paraCsv)
//                            .reduce("", (a, b) -> a.isEmpty() ? b : a + "\n" + b);
//
//            writer.write(conteudo);
//            writer.newLine();
//        }
        try{
            String conteudo = "moeda,valor,coletadoEm\n" +
                    cotacoes.stream()
                            .map(Cotacao::paraCsv)
                            .reduce("", (a, b) -> a.isEmpty() ? b : a + "\n" + b);

            Files.writeString(saida, conteudo);
        }catch(IOException e){
            System.exit(1);
        }
    }
}
