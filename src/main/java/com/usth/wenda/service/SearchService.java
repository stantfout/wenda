package com.usth.wenda.service;
import java.io.IOException;
import	java.util.ArrayList;

import com.usth.wenda.model.Question;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SearchService {

    private static final String SOLR_URL = "http://localhost:8983/solr/wenda";
    private SolrClient client = new HttpSolrClient.Builder(SOLR_URL).build();
    private static final String QUESTION_TITLE_FIELD = "question_title";
    private static final String QUESTION_CONTENT_FIELD = "question_content";

    public List<Question> searchQuestions(String keyword, int offset, int count,
                                          String hlPre, String hlPos) throws Exception {
        List<Question> questionList = new ArrayList<> ();
        SolrQuery query = new SolrQuery(keyword);
        query.setRows(count);
        query.setStart(offset);
        query.setHighlight(true);
        query.setHighlightSimplePre(hlPre);
        query.setHighlightSimplePost(hlPos);
        query.set("hl.fl",QUESTION_CONTENT_FIELD+","+QUESTION_TITLE_FIELD);
        QueryResponse response = client.query(query);
        Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
        for (Map.Entry<String, Map<String, List<String>>> entry : highlighting.entrySet()) {
            Question q = new Question();
            q.setId(Integer.parseInt(entry.getKey()));
            if (entry.getValue().containsKey(QUESTION_CONTENT_FIELD)) {
                List<String> contentList = entry.getValue().get(QUESTION_CONTENT_FIELD);
                if (contentList.size() > 0) {
                    q.setContent(contentList.get(0));
                }
            }
            if (entry.getValue().containsKey(QUESTION_TITLE_FIELD)) {
                List<String> contentList = entry.getValue().get(QUESTION_TITLE_FIELD);
                if (contentList.size() > 0) {
                    q.setTitle(contentList.get(0));
                }
            }
            questionList.add(q);
        }
        return questionList;
    }

    public boolean indexQuestion(int qid, String title, String content) throws IOException, SolrServerException {
        SolrInputDocument document = new SolrInputDocument();
        document.setField("id",qid);
        document.setField(QUESTION_TITLE_FIELD, title);
        document.setField(QUESTION_CONTENT_FIELD, content);
        UpdateResponse add = client.add(document, 1000);
        return add != null && add.getStatus() == 0;
    }
}
