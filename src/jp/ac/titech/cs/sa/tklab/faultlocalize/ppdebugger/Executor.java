package jp.ac.titech.cs.sa.tklab.faultlocalize.ppdebugger;

import java.io.File;
import java.util.concurrent.Callable;

import javax.xml.bind.JAXBException;

import jp.ac.titech.cs.sa.tklab.faultlocalize.ppdebugger.creator.Creator;
import jp.ac.titech.cs.sa.tklab.faultlocalize.ppdebugger.model.execution.ExecutionModel;
import jp.ac.titech.cs.sa.tklab.faultlocalize.ppdebugger.model.learned.LearnedModel;

public class Executor implements Callable<Void>{
	private final LearnedModel passedModel;
	private File file;
	private final int hopNum;
	
	
	public Executor(LearnedModel passedModel,File file,int hopNum){
		this.passedModel = passedModel;
		this.file = file;
		this.hopNum = hopNum;
	}

	@Override
	public Void call() throws Exception {
		try {
			ExecutionModel em = new Creator().createExecutionModel(file, hopNum);
			file = null;
			passedModel.merge(em);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
