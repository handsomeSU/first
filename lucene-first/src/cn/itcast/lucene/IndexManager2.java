package cn.itcast.lucene;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.cn.ChineseAnalyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.DFRSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class IndexManager2 {

	@Test
	public void testAddIndex() throws Exception {
//		1、执行索引库的目录
		Directory  directory = FSDirectory.open(new File("F:\\indexRespo"));
		
//		2、指定分词器  --标准分词器
		Analyzer analyzer = new IKAnalyzer();
		
//		3、创建一个配置对象
		IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);
//		4、创建一个 写入索引对象
		IndexWriter indexWriter = new IndexWriter(directory, config);
		
//		5、写入对象了
		File files = new File("F:\\就业班每天\\web 前端\\框架\\lucene第一天\\Lucene&solr-day01\\资料\\上课用的查询资料searchsource");
		File[] listFiles = files.listFiles();
		for (File file : listFiles) {
			Document doc = new Document();
//			field 域  等同于属性
//			
//			文件名称
			Field  fileNameField = new TextField("name", file.getName(), Store.YES);
			doc.add(fileNameField);
//			文件路径
			Field  filePathField = new StoredField("path", file.getPath());
			doc.add(filePathField);
//			文件大小  单位 b
			long sizeOf = FileUtils.sizeOf(file);
			Field  fileSizeField = new LongField("size", sizeOf, Store.YES);
			doc.add(fileSizeField);
//			文件内容
			String fileContent = FileUtils.readFileToString(file);
			Field  fileContentField = new TextField("content", fileContent, Store.NO);
			doc.add(fileContentField);
			
			indexWriter.addDocument(doc);
		}

//		6、关闭IndexWriter对象
		indexWriter.close();
		
		
	}
	
	@Test
	public void testDeleteIndex() throws Exception {
		Directory directory = FSDirectory.open(new File("F:\\indexRespo"));
		IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, new IKAnalyzer());
		IndexWriter indexWriter = new IndexWriter(directory, config);
		indexWriter.deleteAll();
//		indexWriter.deleteDocuments(new Term("name","apache"));
		indexWriter.close();
	}
	
	@Test
	public void testUpdateIndex() throws Exception {
		Directory directory = FSDirectory.open(new File("F:\\\\indexRespo"));
		IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, new IKAnalyzer());
		IndexWriter indexWriter = new IndexWriter(directory, config);
		Document doc = new Document();
		TextField nameField = new TextField("name","gfdghgfhf自己添加的一个文档IndexWriterConfig自己添加的一个文档spring",Store.YES);
		//设置等级，初始一，越高在网页上越靠前
		nameField.setBoost(20);
		doc.add(nameField);
//		doc.add(new TextField("name", "自己添加的一个文档",Store.YES));
//		doc.add(new StoredField("path", "d://sdffsd"));
//		doc.add(new LongField("size", 100l,Store.YES));
//		doc.add(new StringField("content", "自己添加的一个文档自己添加的一个文档",Store.NO));
//		indexWriter.updateDocument(new Term("name","apache"),doc);
		//--------------------------------
		indexWriter.addDocument(doc);
		indexWriter.close();
	}
	
	@Test
	public void testSearchIndex() throws Exception {
//		1、指定索引库的目录
		Directory  directory = FSDirectory.open(new File("F:\\indexRespo"));
//		2、创建一个读取索引对象
		IndexReader  indexReader = DirectoryReader.open(directory);
//		3、创建一个搜索索引的对象
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
//		4、执行查询
		Query query = new TermQuery(new Term("name", "spring"));
		//查询所有
//		Query query = new MatchAllDocsQuery();
		//文件区间大小
//		Query query = NumericRangeQuery.newLongRange("size", 100l, 1000l, true, true);
		//分词后查询
//		QueryParser queryParser = new QueryParser("name",new IKAnalyzer());
//		Query query = queryParser.parse("spring is a project");
		//多个域查询
//		QueryParser queryParser = new MultiFieldQueryParser(new String[] {"name","content"}, new IKAnalyzer());
//		Query query = queryParser.parse("spring is a project");
		//组合查询     occur存在  must must_not should
//		BooleanQuery query = new BooleanQuery();
//		Query query1 = new TermQuery(new Term("name","spring"));
//		Query query2 = new TermQuery(new Term("content","spring"));
//		query.add(query1, Occur.MUST);
//		query.add(query2, Occur.SHOULD);
		System.out.println("查询语法"+query);
		System.out.println("查询语"+query);
		TopDocs topDocs = indexSearcher.search(query, 100);
		System.out.println("总条数："+topDocs.totalHits);
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		for (ScoreDoc scoreDoc : scoreDocs) {
			int docId = scoreDoc.doc;
			Document doc = indexSearcher.doc(docId);
			System.out.println(doc.get("name"));
//			System.out.println(doc.get("size"));
//			System.out.println(doc.get("path"));
//			System.out.println(doc.get("content"));
			System.out.println("----------------------------------------------");
		}
		
//		5、关闭资源
		indexReader.close();
	}
	
	
	@Test
	public void testAnalyzer() throws Exception {
		
//		毛不易
		
//		Analyzer analyzer = new StandardAnalyzer();
//		Analyzer analyzer = new ChineseAnalyzer(); 
//		C J K
//		Analyzer analyzer = new CJKAnalyzer();
//		Analyzer analyzer = new SmartChineseAnalyzer();
		Analyzer analyzer = new IKAnalyzer();
		
//		StandardAnalyzer:中文的分词 一个字一个字
//		ChineseAnalyzer：过时了
//		CJKAnalyzer:两个字两个字  需要导入 lucene-analyzers-smartcn-4.10.3.jar
//		SmartChineseAnalyzer: 中文还算可以，英文会出现少字母的情况 需要导入 lucene-analyzers-smartcn-4.10.3.jar
		
		
//		String str = "The Spring Framework provides a comprehensive programming and configuration model.";
		String str = "传智播客：MyBatis 本是apache的一个开源项目iBatis,法轮功 2010年这个项目由apache software foundation 迁移到了google code，并且改名为MyBatis--by 白面郎君 毛不易";
		
		TokenStream tokenStream = analyzer.tokenStream("test", str);
		
		tokenStream.reset();  //重置指针
//		添加一个引用 字符串
		CharTermAttribute addAttribute = tokenStream.addAttribute(CharTermAttribute.class);
		
		while(tokenStream.incrementToken()) {
			System.out.println(addAttribute);
		}
	}
	
}
