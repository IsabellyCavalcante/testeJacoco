package testeJacoco;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.ICoverageVisitor;
import org.jacoco.core.data.ExecutionData;
import org.jacoco.core.data.ExecutionDataReader;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.IExecutionDataVisitor;
import org.jacoco.core.data.ISessionInfoVisitor;
import org.jacoco.core.data.SessionInfo;

public class Main {

	private static String sessionName = "";
	private static BufferedWriter bufferedWriter;

	public static void main(String[] args) throws Exception {
		bufferedWriter = new BufferedWriter(new FileWriter("Saida.txt"));
		final FileInputStream in = new FileInputStream("epol2/jacoco.exec");
 
		Map<String, ExecutionDataStore> executions = new HashMap<>();
		
		ISessionInfoVisitor sessionInfoVisitor = new ISessionInfoVisitor() {
			@Override
			public void visitSessionInfo(SessionInfo info) {
				sessionName = info.getId();
			}
		};

		IExecutionDataVisitor executionDataVisitor = new IExecutionDataVisitor() {
			@Override
			public void visitClassExecution(ExecutionData executionData) {
				if (!executionData.getName().startsWith("br/gov/dpf/epol")) {
					return;
				}
				ExecutionDataStore store = executions.getOrDefault(sessionName, new ExecutionDataStore());
				store.put(executionData);
				String saida = sessionName + "   ,   " + executionData.getName() + "\n";
				writeFile(saida);
				executions.put(sessionName, store);
			}
		};

		final ExecutionDataReader reader = new ExecutionDataReader(in);
		reader.setSessionInfoVisitor(sessionInfoVisitor);
		reader.setExecutionDataVisitor(executionDataVisitor);
		reader.read();

		writeFile("fim");
		bufferedWriter.close();
		bufferedWriter = new BufferedWriter(new FileWriter("Saida2.txt"));
		ICoverageVisitor coverageVisitor = new ICoverageVisitor() {

			@Override
			public void visitCoverage(IClassCoverage c) {
				String saida = String.format(
						"package: %s, nomeClasse: %s, primeiraLinha: %s, ultimaLinha: %s, countInstrucao: %s, totalMetodos: %s \n",
						c.getPackageName(), c.getName(), c.getFirstLine(), c.getLastLine(), c.getInstructionCounter(),
						c.getMethods().size());
				writeFile(saida);
				
//				if (c.getName().contains("ChefesDeUnidadeBusinessDelegate")) {
//					System.out.println("branch cont: " + c.getBranchCounter());
//					for (int i = c.getFirstLine(); i < c.getLastLine(); i++) {
//						System.out.println("linha: "+ i);
//						System.out.println("status cover: " + c.getLine(i).getStatus());
//						System.out.println("instruction: " + c.getLine(i).getInstructionCounter().getTotalCount());
//					}
//				}
//				System.out.println(c.getPackageName());
//				System.out.println(c.getName());
//				System.out.println(c.getFirstLine());
//				System.out.println(c.getLine(c.getFirstLine()).getStatus());
//				System.out.println(c.getLastLine());
//				System.out.println(c.getInstructionCounter());
//				System.out.println(c.getMethods().size());
//				System.out.println(c.toString());

			}
		};
		
		for (Entry<String, ExecutionDataStore> entr : executions.entrySet()) {
			writeFile("session: " + entr.getKey() + "\n");
			Analyzer analise = new Analyzer(entr.getValue(), coverageVisitor);
			analise.analyzeAll(".", new File("epol2"));
//			System.out.println("hm: " + analise.analyzeAll(".", new File("epol2")));
//			System.out.println("total classes: " + entr.getValue().getContents().size());
		}
		writeFile("fim");
		bufferedWriter.close();
		in.close();
	}

	public static void writeFile(String string) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("xxx.txt"));
			bufferedWriter.write(string);

		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
}
