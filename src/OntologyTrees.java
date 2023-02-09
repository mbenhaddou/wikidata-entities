import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Tree.ArrayMultiTreeNode;
import Tree.MultiTreeNode;
import Tree.TreeNode;
import utils.Pair;

public class OntologyTrees {
	public TreeNode<String> Professions = null;
	
	public OntologyTrees()
	{
		Professions = new ArrayMultiTreeNode<>("Professions");
		
		File file = new File("data/professions.txt");
		BufferedReader reader = null;

		try {
		    reader = new BufferedReader(new FileReader(file));
		    String text = null;
		    _recurse_tree(Professions, 0, reader);
		    
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		} finally {
		    try {
		        if (reader != null) {
		            reader.close();
		        }
		    } catch (IOException e) {
		    }
		}
		
	}
	
	
private String _recurse_tree(TreeNode parent, int depth, BufferedReader source) throws IOException{
	String last_line="";
	while (last_line!=null && last_line.trim().equals(""))
	{
		last_line= source.readLine();
	}
		while (last_line != null){
		int tabs = getTrailingSpaces(last_line).length();
		if (tabs < depth)
			break;
					
		String[] data = last_line.trim().split(">");
		
		ArrayMultiTreeNode node=new ArrayMultiTreeNode<String>(data[0].replace("\"", ""), data[1]);
		parent.add(node);
					            
		last_line = _recurse_tree(node, tabs+2, source);
			    }
			        
		return last_line;
}
   
	    
	private String getTrailingSpaces(String str) {
		  Pattern p = Pattern.compile("^(\\s+).*");
		  Matcher m = p.matcher(str);
		  String trailing = "";
		  if (m.matches()) {
		    trailing = m.group(1);
		  }
		  return trailing;
		}
	
	public List<Pair<String, String>> getParents(String id)
	{
		TreeNode<String> node = Professions.find(id);
		if(node==null)
			return null;
		List<Pair<String, String>> parents= new ArrayList<Pair<String, String>>();
		parents.add(Pair.createPair(node.data(), node.description()));
		while(node.parent()!=null)
		{
			node = node.parent();
			parents.add(Pair.createPair(node.data(), node.description()));
		}
		return parents;
		
	}
}
