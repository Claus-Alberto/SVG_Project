package com.glauber.svg;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.io.IOException;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SVGMain {

   public static void main(String[] args) throws IOException {
        byte[] bytes;
        double step = 0.5;
        int interacoes = 0;
        int graus = 0;
        Map<String, String> regras = new HashMap<String, String>();
        Matcher matcher;
        String begin = "";
        String stringAtual;
        String primeiraPalavra;
        String segundaPalavra;
        StringBuilder stringBuildavel = new StringBuilder();
        StringBuilder linha;
        StringBuilder build = new StringBuilder();
        Path arquivo = Paths.get("input.txt");
        Path arquivoFinal = Paths.get("result.html");
        Pattern caracteresPermitidos = Pattern.compile("p\\d : *(C|F|\\[|\\]\\+|-|) *-> *([C|F|\\+|\\-|\\[|\\]]+)");
        List<String> lista = Files.readAllLines(arquivo, StandardCharsets.UTF_8);
        List<String> linhas = new ArrayList<>();
        List<Double> x = new ArrayList<>();
        List<Double> y = new ArrayList<>();
        List<Double> angulo = new ArrayList<>();

        for (String linhaDaLista : lista) {
            if (linhaDaLista != null) {
                matcher = caracteresPermitidos.matcher(linhaDaLista);
                primeiraPalavra = linhaDaLista.split(":")[0].strip();
                segundaPalavra = linhaDaLista.split(":")[1].strip();
                if ("n".equals(primeiraPalavra)) interacoes = Integer.parseInt(segundaPalavra);
                else if (matcher.find()) regras.put(matcher.group(1), matcher.group(2));
                else if ("Dg".equals(primeiraPalavra)) graus = Integer.parseInt(segundaPalavra);
                else if ("St".equals(primeiraPalavra)) begin = segundaPalavra;
            }
        }
        stringBuildavel.append(begin);
        for (int count = 1; count <= interacoes; count++) {
            stringAtual = stringBuildavel.toString();
            stringBuildavel.setLength(0);
            for (char letter : stringAtual.toCharArray()) {
                if (!regras.containsKey(String.valueOf(letter))) stringBuildavel.append(String.valueOf(letter));
                else stringBuildavel.append(regras.get(String.valueOf(letter)));
            }
        }
        x.add((double) 40);
        y.add((double) 50);
        angulo.add(Math.toRadians(-60));
        x.add(x.get(x.size() - 1) + (step * Math.cos(angulo.get(angulo.size() - 1))));
        y.add(y.get(y.size() - 1) + (step * Math.sin(angulo.get(angulo.size() - 1))));
        for(char charDaString : stringBuildavel.toString().toCharArray()){
            if(charDaString == 'C' || charDaString == 'F'){
                linha = new StringBuilder();
                linha.append("<linhaDaLista x1=\"");
                linha.append(x.get(x.size()-2));
                linha.append("%\" y1=\"");
                linha.append(y.get(y.size()-2));
                linha.append("%\" x2=\"");
                linha.append(x.get(x.size()-1));
                linha.append("%\" y2=\"");
                linha.append(y.get(y.size()-1));
                linha.append("%\"/>");
                linhas.add(linha.toString());
                x.remove(x.size()-2);
                y.remove(y.size()-2);
                x.add(x.get(x.size() - 1) + (step * Math.cos(angulo.get(angulo.size() - 1))));
                y.add(y.get(y.size() - 1) + (step * Math.sin(angulo.get(angulo.size() - 1))));
            }
            else if(charDaString == ']'){
                x.remove(x.size() - 1);
                x.remove(x.size() - 1);
                y.remove(y.size() - 1);
                y.remove(y.size() - 1);
                angulo.remove(angulo.size() - 1);
            }
            else if(charDaString == '['){
                x.add(x.get(x.size() - 2));
                x.add(x.get(x.size() - 2));
                y.add(y.get(y.size() - 2));
                y.add(y.get(y.size() - 2));
                angulo.add(angulo.get(angulo.size() - 1));
            }
            else if(charDaString == '+' || charDaString == '-'){
                if(charDaString == '+') angulo.add(angulo.get(angulo.size() - 1) - Math.toRadians(graus));
                else if(charDaString == '-') angulo.add(angulo.get(angulo.size() - 1) + Math.toRadians(graus));
                angulo.remove(angulo.size() - 2);
                x.remove(x.size()-1);
                y.remove(y.size()-1);
                x.add(x.get(x.size() - 1) + (step * Math.cos(angulo.get(angulo.size() - 1))));
                y.add(y.get(y.size() - 1) + (step * Math.sin(angulo.get(angulo.size() - 1))));
            }
        }
        build.append("<html><body><div style=\"position: fixed; top: 0; z-index: 1000;\"></div><svg id=\"svgZoom\" viewBox=\"0 0 3000 3000\" preserveAspectRatio=\"xMidYMid meet\" style=\"stroke:black;stroke-width:2\">");
        for(String linhaDaLista : linhas) build.append(linhaDaLista);
        build.append("</svg></body><script>const slider = document.getElementById(\"zoomRange\");const svgZoom = document.getElementById(\"svgZoom\");const zoom = document.getElementById(\"zoom\");slider.oninput = function() {zoom.innerText = `${this.value}%`;svgZoom.style.transform = `scale(${this.value / 100})`;}</script></html>");
        bytes = build.toString().getBytes();
        Files.write(arquivoFinal, bytes);
    }
}