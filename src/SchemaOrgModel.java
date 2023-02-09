import java.io.InputStream;
import java.util.Iterator;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.SimpleSelector;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.tdb.store.DatasetGraphTDB;
import org.apache.jena.tdb.sys.TDBInternal;
import org.apache.jena.util.FileManager;

public class SchemaOrgModel {
	private Model model=null;
	
	public SchemaOrgModel()
	{
		 FileManager fm = FileManager.get();
	        fm.addLocatorClassLoader(SchemaOrgModel.class.getClassLoader());

	        DatasetGraphTDB dsg =  TDBInternal.getBaseDatasetGraphTDB(TDBFactory.createDatasetGraph());
	         model = FileManager.get().loadModel("/Users/mbenhaddou/Documents/Mentis/Developement/Python/wikidata/wiki_schema.nt", null, "NTRIPLES");
	
	}
	
	public String GetSchemaOrgClass(String wikiEntity)
	{
	     
        Resource res =  model.getResource(wikiEntity);
        if(res!=null)
        {
        	 StmtIterator iter =model.listStatements(new SimpleSelector(res, null, (RDFNode) null));
             while (iter.hasNext()) {
                 return iter.toList().toString();
                 }
        }else
        {
        	return "";
        }
       
		return wikiEntity;
		
	}
}
