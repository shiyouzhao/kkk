import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.document.Document;
import org.wltea.analyzer.lucene.IKAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.QueryParser;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by shiyouzhao on 2015/12/12.
 */
public class Lucenetest {

    public static Version luceneVersion = Version.LUCENE_5_3_1;
    Path indexPath = Paths.get("E:\\lucene\\index") ;

    public void creatIndex(){
        IndexWriter writer=null;
        try{
            Directory directior = FSDirectory.open(indexPath);
            IndexWriterConfig cof= new IndexWriterConfig(new StandardAnalyzer());
            Document document = null;
            writer = new IndexWriter(directior,cof);
            File f = new File("E:\\lucene\\source");
            for (File file : f.listFiles()){
                Term tm=new Term("Path",file.getPath());
                writer.deleteDocuments(tm);
                document = new Document();
                document.add(new StringField("fileName",file.getName(), Field.Store.YES));
                document.add(new StringField("Path",file.getPath(),Field.Store.YES));
                document.add(new TextField("contents",readFileContent(file),Field.Store.YES));
                writer.addDocument(document);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                writer.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    public String readFileContent(File file) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"GBK"));
            //存储File的内容
            StringBuffer content = new StringBuffer();

            for(String line = null;(line = reader.readLine()) != null;) {
                content.append(line).append("\n");
            }

            return content.toString();

        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
    public void  indexSearch(){
        DirectoryReader reder = null;
        try{
            Directory directory = FSDirectory.open(indexPath);

            reder = DirectoryReader.open(directory);

            IndexSearcher search = new IndexSearcher(reder);

            QueryParser  parser = new QueryParser ("contents",new StandardAnalyzer());
            Query query = parser.parse("11");

            TopDocs tpd = search.search(query,10);

            ScoreDoc[] doc = tpd.scoreDocs;

            System.out.println("begin:"+tpd.totalHits);

            for (ScoreDoc s:doc){
                Document d = search.doc(s.doc);
                System.out.println(d.get("fileName")+s.score+"\n  + "+d.get("contents"));
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                reder.close();
            }catch (IOException e){

                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Lucenetest m = new Lucenetest();
        m.creatIndex();
        m.indexSearch();
    }
}
