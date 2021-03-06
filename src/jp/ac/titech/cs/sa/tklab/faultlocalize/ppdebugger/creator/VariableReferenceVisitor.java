package jp.ac.titech.cs.sa.tklab.faultlocalize.ppdebugger.creator;


import jp.ac.nagoya_u.is.i.agusa.person.knhr.bxmodel.VariableDefinition;
import jp.ac.nagoya_u.is.i.agusa.person.knhr.bxmodel.VariableReference;
import jp.ac.titech.cs.sa.tklab.faultlocalize.StatementData;
import jp.ac.titech.cs.sa.tklab.faultlocalize.StatementDataFactory;
import jp.ac.titech.cs.sa.tklab.faultlocalize.ppdebugger.DataDependencyFactory;
import jp.ac.titech.cs.sa.tklab.faultlocalize.ppdebugger.creator.scope.Scope;
import jp.ac.titech.cs.sa.tklab.faultlocalize.ppdebugger.model.DataDependency;
import jp.ac.titech.cs.sa.tklab.faultlocalize.ppdebugger.model.DataDependencySet;
import jp.ac.titech.cs.sa.tklab.faultlocalize.ppdebugger.model.execution.ExecutionModel;
import jp.ac.titech.cs.sa.tklab.faultlocalize.ppdebugger.model.execution.LineVariable;

public class VariableReferenceVisitor {
	static void create(ExecutionModel em,VariableReference ref,Scope scope,LineVariable refLine,LineVariable argsLine){
		StatementDataFactory sdFactory = StatementDataFactory.getInstance();
		StatementData currentSd = sdFactory.genStatementData(ref.getSourcePath(),ref.getLineNumber(),ref.getThread());
		em.addStatementData(currentSd);
		
		if(isSkip(ref)) return;

		DataDependencyFactory ddFactory = DataDependencyFactory.getInstance();
		String varName = NameCreator.createVariableName(ref, scope);
		if(em.getVariable(varName) == null){		//メソッドの引数などは定義なしに参照され得る
			return;
		}
		//最後の定義情報から依存元のステートメントを得る
		VariableDefinition vd = em.getVariable(varName).getLatestDefinition();
		StatementData fromSd = sdFactory.genStatementData(vd.getSourcePath(),vd.getLineNumber(),vd.getThread());
		
		//データ依存を作成
		DataDependency dd = ddFactory.genDataDependency(NameCreator.createVariableName(ref, scope),scope.getTreeNode(),fromSd);
		DataDependencySet dds = new DataDependencySet(currentSd,dd,ref.getEventNumber());
		
		//依存先のステートメントにデータ依存を追加
		em.addDataDependencySet(currentSd, dds);
		refLine.add(currentSd, em.getVariable(varName));
		argsLine.add(currentSd, em.getVariable(varName));
	}
	
	/**
	 * 変数名がわからない場合は無視する
	 * @param ref
	 * @return　無視するときにtrue
	 */
	private static boolean isSkip(VariableReference ref){
		if(ref.getReferredVariable().getFieldInfo() != null
				&& ref.getReferredVariable().getFieldInfo().getVariableName() == null){
			return true;
		}
		if(ref.getReferredVariable().getLocalVariableInfo() != null
				&& ref.getReferredVariable().getLocalVariableInfo().getVariableName() == null){
			return true;
		}
		return false;
	}

}
