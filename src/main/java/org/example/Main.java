package org.example;

import java.util.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        System.out.printf("Hello and welcome!");

        for (int i = 1; i <= 5; i++) {
            //TIP Press <shortcut actionId="Debug"/> to start debugging your code. We have set one <icon src="AllIcons.Debugger.Db_set_breakpoint"/> breakpoint
            // for you, but you can always add more by pressing <shortcut actionId="ToggleLineBreakpoint"/>.
            System.out.println("i = " + i);
        }
        queue();
    }

    public static void queue(){
        Queue<String> fila = new LinkedList<>();
        Deque<String> pila = new ArrayDeque<>();

        pila.push("cliente 1");
        pila.push("cliente 2");

        fila.add("cliente 2");
        fila.add("cliente 1");
        fila.offer("cliente 3");
        fila.add("cliente 0");

        System.out.println("Cliente fuera:"+fila.poll());
        System.out.println(("Sig Cliente:" + fila.peek()));

        for(String cliente: fila){
            System.out.println("Cliente: "+ cliente);
        }
    }

    public static void stringBuilder(){
        StringBuilder sb = new StringBuilder();

        sb.append("holas");
        sb.append("adios");

        //sb.reverse();
        sb.replace(0,2,"jjjj");

        System.out.println("StringBuilder: "+sb);
    }

    public static void arrayList(){
        List<Integer> numeros = new ArrayList<>();

        numeros.add(1);
        numeros.add(2);

        for(int i=0;i<numeros.size();i++) {
            System.out.println("numero: " + numeros.get(i));
        }
    }

    public static void edades(){
        Map<String,Integer> edades = new HashMap<>();

        edades.put("Carlo",6);
        edades.put("Jose",40);
        edades.put("Carlo",8);// se actualiza el key existente

        int edad = edades.getOrDefault("Carlo",0);
        System.out.println("Edad de Carlo: "+edad);
        procesarMapa(edades);
    }

    public static void procesarMapa(Map<String,Integer> edades){
        for(String nombre: edades.keySet()){
            System.out.println(nombre + " -> " + edades.get(nombre));
        }
        for(Integer edad: edades.values()){
            System.out.println("edad: "+edad);
        }
    }

    public static void hashSet() {
        Set<Character> sc = new HashSet<>();

        sc.add('a');
        sc.add('e');
        sc.add('i');
        sc.add('e');
        sc.add('i');
        sc.add('u');

        Object[] ch = sc.toArray();

        Arrays.sort(ch);

        for (Object c : ch) {
            System.out.println("letra: " + c);
        }
    }
}