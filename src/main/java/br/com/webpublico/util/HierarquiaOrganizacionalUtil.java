package br.com.webpublico.util;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class HierarquiaOrganizacionalUtil {

    public static List<String> removeZerosFinalCodigos(List<String> codigos) {
        String regex = "[\\.0]+$";
        return codigos.stream()
            .map(codigo -> codigo.replaceAll(regex, ""))
            .sorted(Comparator.comparingInt(String::length))
            .collect(Collectors.toList());
    }

    public static String removeZerosFinalCodigo(String codigo) {
        String regex = "[\\.0]+$";
        return codigo.replaceAll(regex, "");
    }
}
