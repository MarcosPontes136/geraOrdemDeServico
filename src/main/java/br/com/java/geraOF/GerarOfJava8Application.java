package br.com.java.geraOF;

import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class GerarOfJava8Application {

	public static void main(String[] args) {
		String continua;
		
		Scanner scanner = new Scanner(System.in);
		
		do {
			
			System.out.println("Digite o caminho da pasta: ");
			String caminho = scanner.nextLine();
			
			System.out.println("Digite o comando a ser executado: ");
			String comando = scanner.nextLine();
			
			executarComando(caminho, comando);
			
			System.out.println("Gostaria de tentar novamente? (S)ou(N)");
			continua = scanner.nextLine();
			
		} while (continua.equalsIgnoreCase("S") );
		
		System.out.println("---------------!!!Fechando programa!!!---------------");
		scanner.close();
	}
	
	public static void executarComando(String caminho, String comando) {
		File directory = new File(caminho);
		
		if (directory.exists() && directory.isDirectory()) {
			try {
				
				ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", comando);
				processBuilder.directory(directory);
				
				Process processo = processBuilder.start();
				
				processo.waitFor();
				
				File arquivo = new File(directory, "ENTREGA.txt");
				if (arquivo.exists()) {
					System.out.println("\nO arquivo ENTREGA.txt foi criado com sucesso na pasta.\n");
					
					ProcessBuilder start = new ProcessBuilder("cmd.exe", "/c", "start", "ENTREGA.txt");
					start.directory(directory);
					start.start();
					extrairCommit(arquivo);
				} else {
					System.out.println("\nO arquivo ENTREGA.txt não foi criado na pasta.\n");
				}
				
				processo.destroy();
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("O diretório não existe ou não é uma pasta válida.");
		}
	}
	
	public static void extrairCommit(File arquivo) {
		try (BufferedReader bReader = new BufferedReader(new FileReader(arquivo))) {
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(arquivo.getParentFile() ,"ENTREGA_FORMATADA.txt")));
			
			String linha;
			
			String commitHash = null;
			
			String taskNumber = null;
			
			File outputFile = new File(arquivo.getParentFile(), "ENTREGA_FORMATADA.txt");
			
	        String parentDirectory = arquivo.getParent();
	        
	        // Obtém a última parte do caminho após a última barra "/"
	        String lastPartOfPath = parentDirectory.substring(parentDirectory.lastIndexOf(File.separator) + 1);
			
			while ((linha = bReader.readLine()) != null) {
				
				if (linha.startsWith("'commit:")) {
					
					commitHash = linha.substring(8, Math.min(18, linha.length())).trim();
					
					String formatCommit = "\nInformações do commit: " + commitHash + "\n";
					
					writer.write(formatCommit);
					
                } else if (linha.contains("Message:task")) {
                	
                    taskNumber = linha.substring(13, Math.min(20, linha.length())).trim();
                    
				} else if (commitHash != null) {
										
					String formatado = lastPartOfPath + "/" + linha.substring(Math.min(2, linha.length())).trim() + "#" + commitHash + ";" + taskNumber +"\n";
					 
					writer.write(formatado);
				}
			}
			
	        writer.flush();

	        String command;
	        
	        if (System.getProperty("os.name").startsWith("Windows")) {
	            // Comando para Windows (Bloco de Notas)
	            command = "notepad";
	        } else {
	            // Comando para Linux (editor de texto padrão)
	            command = "xdg-open"; // Este comando pode variar dependendo da distribuição Linux
	        }
	        
	        ProcessBuilder startForm = new ProcessBuilder(command, outputFile.getAbsolutePath());
	        startForm.start();
	        
	    } catch (HeadlessException | IOException e) {
	        e.printStackTrace();
	    }
	}
}
