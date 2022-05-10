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
        int interacoes = 0;
        String begin = "";
        int graus = 0;
        String stringAtual;
        Matcher matcher;
        String primeiraPalavra;
        String segundaPalavra;
        Path arquivo = Paths.get("input.txt");
        List<String> lista = Files.readAllLines(arquivo, StandardCharsets.UTF_8);
        Pattern caracteresPermitidos = Pattern.compile("p\\d : *(C|F|\\[|\\]\\+|-|) *-> *([C|F|\\+|\\-|\\[|\\]]+)");
        StringBuilder stringBuildavel = new StringBuilder();
        Map<String, String> regras = new HashMap<String, String>();
        List<Double> x = new ArrayList<>();
        List<Double> y = new ArrayList<>();
        List<Double> angulo = new ArrayList<>();
        List<String> linhas = new ArrayList<>();
        double step = 0.5;
        StringBuilder linha;
        Path arquivoFinal;
        StringBuilder build = new StringBuilder();
        byte[] bytes;

        for (String line : lista) {
            if (line != null) {
                matcher = caracteresPermitidos.matcher(line);
                primeiraPalavra = line.split(":")[0].strip();
                segundaPalavra = line.split(":")[1].strip();
                if ("n".equals(primeiraPalavra)) interacoes = Integer.parseInt(segundaPalavra);
                else if (matcher.find()) regras.put(matcher.group(1), matcher.group(2));
                else if ("Dg".equals(primeiraPalavra)) graus = Integer.parseInt(segundaPalavra);
                else if ("St".equals(primeiraPalavra)) begin = segundaPalavra;
            }
        }
        stringBuildavel.append(begin);
        System.out.println("n = 0: " + begin);
        for (int i = 1; i <= interacoes; i++) {
            stringAtual = stringBuildavel.toString();
            stringBuildavel.setLength(0);
            for (char letter : stringAtual.toCharArray()) {
                if (!regras.containsKey(String.valueOf(letter))) stringBuildavel.append(String.valueOf(letter));
                else stringBuildavel.append(regras.get(String.valueOf(letter)));
            }
            System.out.println(stringBuildavel.toString());
        }
        x.add((double) 40);
        y.add((double) 50);
        angulo.add(Math.toRadians(-60));
        x.add(x.get(x.size() - 1) + (step * Math.cos(angulo.get(angulo.size() - 1))));
        y.add(y.get(y.size() - 1) + (step * Math.sin(angulo.get(angulo.size() - 1))));
        for(char c : stringBuildavel.toString().toCharArray()){
            if(c == 'C' || c == 'F'){
                linha = new StringBuilder();
                linha.append("<line x1=\"");
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
            else if(c == ']'){
                x.remove(x.size() - 1);
                x.remove(x.size() - 1);
                y.remove(y.size() - 1);
                y.remove(y.size() - 1);
                angulo.remove(angulo.size() - 1);
            }
            else if(c == '['){
                x.add(x.get(x.size() - 2));
                x.add(x.get(x.size() - 2));
                y.add(y.get(y.size() - 2));
                y.add(y.get(y.size() - 2));
                angulo.add(angulo.get(angulo.size() - 1));
            }
            else if(c == '+' || c == '-'){
                if(c == '+') angulo.add(angulo.get(angulo.size() - 1) - Math.toRadians(graus));
                else if(c == '-') angulo.add(angulo.get(angulo.size() - 1) + Math.toRadians(graus));
                angulo.remove(angulo.size() - 2);
                x.remove(x.size()-1);
                y.remove(y.size()-1);
                x.add(x.get(x.size() - 1) + (step * Math.cos(angulo.get(angulo.size() - 1))));
                y.add(y.get(y.size() - 1) + (step * Math.sin(angulo.get(angulo.size() - 1))));
            }
        }
        arquivoFinal = Paths.get("result.html");
        build.append("<html><body style=\"background-color:white;\"><div style=\"position: fixed; top: 0; z-index: 1000;\"></div><svg id=\"svgZoom\" viewBox=\"0 0 3000 3000\" preserveAspectRatio=\"xMidYMid meet\" style=\"stroke:black;stroke-width:2\">");
        for(String line : linhas) build.append(line);
        build.append("</svg></body><script>const slider = document.getElementById(\"zoomRange\");const svgZoom = document.getElementById(\"svgZoom\");const zoom = document.getElementById(\"zoom\");slider.oninput = function() {zoom.innerText = `${this.value}%`;svgZoom.style.transform = `scale(${this.value / 100})`;}</script></html>");
        bytes = build.toString().getBytes();
        Files.write(arquivoFinal, bytes);
    }
}