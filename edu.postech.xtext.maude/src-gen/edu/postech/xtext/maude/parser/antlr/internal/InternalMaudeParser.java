package edu.postech.xtext.maude.parser.antlr.internal;

import org.eclipse.xtext.*;
import org.eclipse.xtext.parser.*;
import org.eclipse.xtext.parser.impl.*;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.parser.antlr.AbstractInternalAntlrParser;
import org.eclipse.xtext.parser.antlr.XtextTokenStream;
import org.eclipse.xtext.parser.antlr.XtextTokenStream.HiddenTokens;
import org.eclipse.xtext.parser.antlr.AntlrDatatypeRuleToken;
import edu.postech.xtext.maude.services.MaudeGrammarAccess;



import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class InternalMaudeParser extends AbstractInternalAntlrParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "RULE_STRING", "RULE_ID", "RULE_INT", "RULE_ML_COMMENT", "RULE_SL_COMMENT", "RULE_WS", "RULE_ANY_OTHER", "'Maude Directory'", "':'", "';'", "'Maude'", "'Options'"
    };
    public static final int RULE_ID=5;
    public static final int RULE_WS=9;
    public static final int RULE_STRING=4;
    public static final int RULE_ANY_OTHER=10;
    public static final int RULE_SL_COMMENT=8;
    public static final int T__15=15;
    public static final int RULE_INT=6;
    public static final int T__11=11;
    public static final int RULE_ML_COMMENT=7;
    public static final int T__12=12;
    public static final int T__13=13;
    public static final int T__14=14;
    public static final int EOF=-1;

    // delegates
    // delegators


        public InternalMaudeParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public InternalMaudeParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return InternalMaudeParser.tokenNames; }
    public String getGrammarFileName() { return "InternalMaude.g"; }



     	private MaudeGrammarAccess grammarAccess;

        public InternalMaudeParser(TokenStream input, MaudeGrammarAccess grammarAccess) {
            this(input);
            this.grammarAccess = grammarAccess;
            registerRules(grammarAccess.getGrammar());
        }

        @Override
        protected String getFirstRuleName() {
        	return "Model";
       	}

       	@Override
       	protected MaudeGrammarAccess getGrammarAccess() {
       		return grammarAccess;
       	}




    // $ANTLR start "entryRuleModel"
    // InternalMaude.g:64:1: entryRuleModel returns [EObject current=null] : iv_ruleModel= ruleModel EOF ;
    public final EObject entryRuleModel() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleModel = null;


        try {
            // InternalMaude.g:64:46: (iv_ruleModel= ruleModel EOF )
            // InternalMaude.g:65:2: iv_ruleModel= ruleModel EOF
            {
             newCompositeNode(grammarAccess.getModelRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleModel=ruleModel();

            state._fsp--;

             current =iv_ruleModel; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleModel"


    // $ANTLR start "ruleModel"
    // InternalMaude.g:71:1: ruleModel returns [EObject current=null] : ( ( ( ( ({...}? => ( ({...}? => (otherlv_1= 'Maude Directory' otherlv_2= ':' ( (lv_path_3_0= RULE_STRING ) ) otherlv_4= ';' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_5= 'Maude' otherlv_6= ':' ( (lv_Maude_7_0= RULE_STRING ) ) otherlv_8= ';' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_9= 'Options' otherlv_10= ':' ( (lv_options_11_0= RULE_STRING ) )* otherlv_12= ';' ) ) ) ) )+ {...}?) ) ) ;
    public final EObject ruleModel() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_2=null;
        Token lv_path_3_0=null;
        Token otherlv_4=null;
        Token otherlv_5=null;
        Token otherlv_6=null;
        Token lv_Maude_7_0=null;
        Token otherlv_8=null;
        Token otherlv_9=null;
        Token otherlv_10=null;
        Token lv_options_11_0=null;
        Token otherlv_12=null;


        	enterRule();

        try {
            // InternalMaude.g:77:2: ( ( ( ( ( ({...}? => ( ({...}? => (otherlv_1= 'Maude Directory' otherlv_2= ':' ( (lv_path_3_0= RULE_STRING ) ) otherlv_4= ';' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_5= 'Maude' otherlv_6= ':' ( (lv_Maude_7_0= RULE_STRING ) ) otherlv_8= ';' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_9= 'Options' otherlv_10= ':' ( (lv_options_11_0= RULE_STRING ) )* otherlv_12= ';' ) ) ) ) )+ {...}?) ) ) )
            // InternalMaude.g:78:2: ( ( ( ( ({...}? => ( ({...}? => (otherlv_1= 'Maude Directory' otherlv_2= ':' ( (lv_path_3_0= RULE_STRING ) ) otherlv_4= ';' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_5= 'Maude' otherlv_6= ':' ( (lv_Maude_7_0= RULE_STRING ) ) otherlv_8= ';' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_9= 'Options' otherlv_10= ':' ( (lv_options_11_0= RULE_STRING ) )* otherlv_12= ';' ) ) ) ) )+ {...}?) ) )
            {
            // InternalMaude.g:78:2: ( ( ( ( ({...}? => ( ({...}? => (otherlv_1= 'Maude Directory' otherlv_2= ':' ( (lv_path_3_0= RULE_STRING ) ) otherlv_4= ';' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_5= 'Maude' otherlv_6= ':' ( (lv_Maude_7_0= RULE_STRING ) ) otherlv_8= ';' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_9= 'Options' otherlv_10= ':' ( (lv_options_11_0= RULE_STRING ) )* otherlv_12= ';' ) ) ) ) )+ {...}?) ) )
            // InternalMaude.g:79:3: ( ( ( ({...}? => ( ({...}? => (otherlv_1= 'Maude Directory' otherlv_2= ':' ( (lv_path_3_0= RULE_STRING ) ) otherlv_4= ';' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_5= 'Maude' otherlv_6= ':' ( (lv_Maude_7_0= RULE_STRING ) ) otherlv_8= ';' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_9= 'Options' otherlv_10= ':' ( (lv_options_11_0= RULE_STRING ) )* otherlv_12= ';' ) ) ) ) )+ {...}?) )
            {
            // InternalMaude.g:79:3: ( ( ( ({...}? => ( ({...}? => (otherlv_1= 'Maude Directory' otherlv_2= ':' ( (lv_path_3_0= RULE_STRING ) ) otherlv_4= ';' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_5= 'Maude' otherlv_6= ':' ( (lv_Maude_7_0= RULE_STRING ) ) otherlv_8= ';' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_9= 'Options' otherlv_10= ':' ( (lv_options_11_0= RULE_STRING ) )* otherlv_12= ';' ) ) ) ) )+ {...}?) )
            // InternalMaude.g:80:4: ( ( ({...}? => ( ({...}? => (otherlv_1= 'Maude Directory' otherlv_2= ':' ( (lv_path_3_0= RULE_STRING ) ) otherlv_4= ';' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_5= 'Maude' otherlv_6= ':' ( (lv_Maude_7_0= RULE_STRING ) ) otherlv_8= ';' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_9= 'Options' otherlv_10= ':' ( (lv_options_11_0= RULE_STRING ) )* otherlv_12= ';' ) ) ) ) )+ {...}?)
            {
             
            			  getUnorderedGroupHelper().enter(grammarAccess.getModelAccess().getUnorderedGroup());
            			
            // InternalMaude.g:83:4: ( ( ({...}? => ( ({...}? => (otherlv_1= 'Maude Directory' otherlv_2= ':' ( (lv_path_3_0= RULE_STRING ) ) otherlv_4= ';' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_5= 'Maude' otherlv_6= ':' ( (lv_Maude_7_0= RULE_STRING ) ) otherlv_8= ';' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_9= 'Options' otherlv_10= ':' ( (lv_options_11_0= RULE_STRING ) )* otherlv_12= ';' ) ) ) ) )+ {...}?)
            // InternalMaude.g:84:5: ( ({...}? => ( ({...}? => (otherlv_1= 'Maude Directory' otherlv_2= ':' ( (lv_path_3_0= RULE_STRING ) ) otherlv_4= ';' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_5= 'Maude' otherlv_6= ':' ( (lv_Maude_7_0= RULE_STRING ) ) otherlv_8= ';' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_9= 'Options' otherlv_10= ':' ( (lv_options_11_0= RULE_STRING ) )* otherlv_12= ';' ) ) ) ) )+ {...}?
            {
            // InternalMaude.g:84:5: ( ({...}? => ( ({...}? => (otherlv_1= 'Maude Directory' otherlv_2= ':' ( (lv_path_3_0= RULE_STRING ) ) otherlv_4= ';' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_5= 'Maude' otherlv_6= ':' ( (lv_Maude_7_0= RULE_STRING ) ) otherlv_8= ';' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_9= 'Options' otherlv_10= ':' ( (lv_options_11_0= RULE_STRING ) )* otherlv_12= ';' ) ) ) ) )+
            int cnt2=0;
            loop2:
            do {
                int alt2=4;
                int LA2_0 = input.LA(1);

                if ( LA2_0 == 11 && getUnorderedGroupHelper().canSelect(grammarAccess.getModelAccess().getUnorderedGroup(), 0) ) {
                    alt2=1;
                }
                else if ( LA2_0 == 14 && getUnorderedGroupHelper().canSelect(grammarAccess.getModelAccess().getUnorderedGroup(), 1) ) {
                    alt2=2;
                }
                else if ( LA2_0 == 15 && getUnorderedGroupHelper().canSelect(grammarAccess.getModelAccess().getUnorderedGroup(), 2) ) {
                    alt2=3;
                }


                switch (alt2) {
            	case 1 :
            	    // InternalMaude.g:85:3: ({...}? => ( ({...}? => (otherlv_1= 'Maude Directory' otherlv_2= ':' ( (lv_path_3_0= RULE_STRING ) ) otherlv_4= ';' ) ) ) )
            	    {
            	    // InternalMaude.g:85:3: ({...}? => ( ({...}? => (otherlv_1= 'Maude Directory' otherlv_2= ':' ( (lv_path_3_0= RULE_STRING ) ) otherlv_4= ';' ) ) ) )
            	    // InternalMaude.g:86:4: {...}? => ( ({...}? => (otherlv_1= 'Maude Directory' otherlv_2= ':' ( (lv_path_3_0= RULE_STRING ) ) otherlv_4= ';' ) ) )
            	    {
            	    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getModelAccess().getUnorderedGroup(), 0) ) {
            	        throw new FailedPredicateException(input, "ruleModel", "getUnorderedGroupHelper().canSelect(grammarAccess.getModelAccess().getUnorderedGroup(), 0)");
            	    }
            	    // InternalMaude.g:86:99: ( ({...}? => (otherlv_1= 'Maude Directory' otherlv_2= ':' ( (lv_path_3_0= RULE_STRING ) ) otherlv_4= ';' ) ) )
            	    // InternalMaude.g:87:5: ({...}? => (otherlv_1= 'Maude Directory' otherlv_2= ':' ( (lv_path_3_0= RULE_STRING ) ) otherlv_4= ';' ) )
            	    {

            	    					getUnorderedGroupHelper().select(grammarAccess.getModelAccess().getUnorderedGroup(), 0);
            	    				
            	    // InternalMaude.g:90:8: ({...}? => (otherlv_1= 'Maude Directory' otherlv_2= ':' ( (lv_path_3_0= RULE_STRING ) ) otherlv_4= ';' ) )
            	    // InternalMaude.g:90:9: {...}? => (otherlv_1= 'Maude Directory' otherlv_2= ':' ( (lv_path_3_0= RULE_STRING ) ) otherlv_4= ';' )
            	    {
            	    if ( !((true)) ) {
            	        throw new FailedPredicateException(input, "ruleModel", "true");
            	    }
            	    // InternalMaude.g:90:18: (otherlv_1= 'Maude Directory' otherlv_2= ':' ( (lv_path_3_0= RULE_STRING ) ) otherlv_4= ';' )
            	    // InternalMaude.g:90:19: otherlv_1= 'Maude Directory' otherlv_2= ':' ( (lv_path_3_0= RULE_STRING ) ) otherlv_4= ';'
            	    {
            	    otherlv_1=(Token)match(input,11,FOLLOW_3); 

            	    								newLeafNode(otherlv_1, grammarAccess.getModelAccess().getMaudeDirectoryKeyword_0_0());
            	    							
            	    otherlv_2=(Token)match(input,12,FOLLOW_4); 

            	    								newLeafNode(otherlv_2, grammarAccess.getModelAccess().getColonKeyword_0_1());
            	    							
            	    // InternalMaude.g:98:8: ( (lv_path_3_0= RULE_STRING ) )
            	    // InternalMaude.g:99:9: (lv_path_3_0= RULE_STRING )
            	    {
            	    // InternalMaude.g:99:9: (lv_path_3_0= RULE_STRING )
            	    // InternalMaude.g:100:10: lv_path_3_0= RULE_STRING
            	    {
            	    lv_path_3_0=(Token)match(input,RULE_STRING,FOLLOW_5); 

            	    										newLeafNode(lv_path_3_0, grammarAccess.getModelAccess().getPathSTRINGTerminalRuleCall_0_2_0());
            	    									

            	    										if (current==null) {
            	    											current = createModelElement(grammarAccess.getModelRule());
            	    										}
            	    										setWithLastConsumed(
            	    											current,
            	    											"path",
            	    											lv_path_3_0,
            	    											"org.eclipse.xtext.common.Terminals.STRING");
            	    									

            	    }


            	    }

            	    otherlv_4=(Token)match(input,13,FOLLOW_6); 

            	    								newLeafNode(otherlv_4, grammarAccess.getModelAccess().getSemicolonKeyword_0_3());
            	    							

            	    }


            	    }

            	     
            	    					getUnorderedGroupHelper().returnFromSelection(grammarAccess.getModelAccess().getUnorderedGroup());
            	    				

            	    }


            	    }


            	    }
            	    break;
            	case 2 :
            	    // InternalMaude.g:126:3: ({...}? => ( ({...}? => (otherlv_5= 'Maude' otherlv_6= ':' ( (lv_Maude_7_0= RULE_STRING ) ) otherlv_8= ';' ) ) ) )
            	    {
            	    // InternalMaude.g:126:3: ({...}? => ( ({...}? => (otherlv_5= 'Maude' otherlv_6= ':' ( (lv_Maude_7_0= RULE_STRING ) ) otherlv_8= ';' ) ) ) )
            	    // InternalMaude.g:127:4: {...}? => ( ({...}? => (otherlv_5= 'Maude' otherlv_6= ':' ( (lv_Maude_7_0= RULE_STRING ) ) otherlv_8= ';' ) ) )
            	    {
            	    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getModelAccess().getUnorderedGroup(), 1) ) {
            	        throw new FailedPredicateException(input, "ruleModel", "getUnorderedGroupHelper().canSelect(grammarAccess.getModelAccess().getUnorderedGroup(), 1)");
            	    }
            	    // InternalMaude.g:127:99: ( ({...}? => (otherlv_5= 'Maude' otherlv_6= ':' ( (lv_Maude_7_0= RULE_STRING ) ) otherlv_8= ';' ) ) )
            	    // InternalMaude.g:128:5: ({...}? => (otherlv_5= 'Maude' otherlv_6= ':' ( (lv_Maude_7_0= RULE_STRING ) ) otherlv_8= ';' ) )
            	    {

            	    					getUnorderedGroupHelper().select(grammarAccess.getModelAccess().getUnorderedGroup(), 1);
            	    				
            	    // InternalMaude.g:131:8: ({...}? => (otherlv_5= 'Maude' otherlv_6= ':' ( (lv_Maude_7_0= RULE_STRING ) ) otherlv_8= ';' ) )
            	    // InternalMaude.g:131:9: {...}? => (otherlv_5= 'Maude' otherlv_6= ':' ( (lv_Maude_7_0= RULE_STRING ) ) otherlv_8= ';' )
            	    {
            	    if ( !((true)) ) {
            	        throw new FailedPredicateException(input, "ruleModel", "true");
            	    }
            	    // InternalMaude.g:131:18: (otherlv_5= 'Maude' otherlv_6= ':' ( (lv_Maude_7_0= RULE_STRING ) ) otherlv_8= ';' )
            	    // InternalMaude.g:131:19: otherlv_5= 'Maude' otherlv_6= ':' ( (lv_Maude_7_0= RULE_STRING ) ) otherlv_8= ';'
            	    {
            	    otherlv_5=(Token)match(input,14,FOLLOW_3); 

            	    								newLeafNode(otherlv_5, grammarAccess.getModelAccess().getMaudeKeyword_1_0());
            	    							
            	    otherlv_6=(Token)match(input,12,FOLLOW_4); 

            	    								newLeafNode(otherlv_6, grammarAccess.getModelAccess().getColonKeyword_1_1());
            	    							
            	    // InternalMaude.g:139:8: ( (lv_Maude_7_0= RULE_STRING ) )
            	    // InternalMaude.g:140:9: (lv_Maude_7_0= RULE_STRING )
            	    {
            	    // InternalMaude.g:140:9: (lv_Maude_7_0= RULE_STRING )
            	    // InternalMaude.g:141:10: lv_Maude_7_0= RULE_STRING
            	    {
            	    lv_Maude_7_0=(Token)match(input,RULE_STRING,FOLLOW_5); 

            	    										newLeafNode(lv_Maude_7_0, grammarAccess.getModelAccess().getMaudeSTRINGTerminalRuleCall_1_2_0());
            	    									

            	    										if (current==null) {
            	    											current = createModelElement(grammarAccess.getModelRule());
            	    										}
            	    										setWithLastConsumed(
            	    											current,
            	    											"Maude",
            	    											lv_Maude_7_0,
            	    											"org.eclipse.xtext.common.Terminals.STRING");
            	    									

            	    }


            	    }

            	    otherlv_8=(Token)match(input,13,FOLLOW_6); 

            	    								newLeafNode(otherlv_8, grammarAccess.getModelAccess().getSemicolonKeyword_1_3());
            	    							

            	    }


            	    }

            	     
            	    					getUnorderedGroupHelper().returnFromSelection(grammarAccess.getModelAccess().getUnorderedGroup());
            	    				

            	    }


            	    }


            	    }
            	    break;
            	case 3 :
            	    // InternalMaude.g:167:3: ({...}? => ( ({...}? => (otherlv_9= 'Options' otherlv_10= ':' ( (lv_options_11_0= RULE_STRING ) )* otherlv_12= ';' ) ) ) )
            	    {
            	    // InternalMaude.g:167:3: ({...}? => ( ({...}? => (otherlv_9= 'Options' otherlv_10= ':' ( (lv_options_11_0= RULE_STRING ) )* otherlv_12= ';' ) ) ) )
            	    // InternalMaude.g:168:4: {...}? => ( ({...}? => (otherlv_9= 'Options' otherlv_10= ':' ( (lv_options_11_0= RULE_STRING ) )* otherlv_12= ';' ) ) )
            	    {
            	    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getModelAccess().getUnorderedGroup(), 2) ) {
            	        throw new FailedPredicateException(input, "ruleModel", "getUnorderedGroupHelper().canSelect(grammarAccess.getModelAccess().getUnorderedGroup(), 2)");
            	    }
            	    // InternalMaude.g:168:99: ( ({...}? => (otherlv_9= 'Options' otherlv_10= ':' ( (lv_options_11_0= RULE_STRING ) )* otherlv_12= ';' ) ) )
            	    // InternalMaude.g:169:5: ({...}? => (otherlv_9= 'Options' otherlv_10= ':' ( (lv_options_11_0= RULE_STRING ) )* otherlv_12= ';' ) )
            	    {

            	    					getUnorderedGroupHelper().select(grammarAccess.getModelAccess().getUnorderedGroup(), 2);
            	    				
            	    // InternalMaude.g:172:8: ({...}? => (otherlv_9= 'Options' otherlv_10= ':' ( (lv_options_11_0= RULE_STRING ) )* otherlv_12= ';' ) )
            	    // InternalMaude.g:172:9: {...}? => (otherlv_9= 'Options' otherlv_10= ':' ( (lv_options_11_0= RULE_STRING ) )* otherlv_12= ';' )
            	    {
            	    if ( !((true)) ) {
            	        throw new FailedPredicateException(input, "ruleModel", "true");
            	    }
            	    // InternalMaude.g:172:18: (otherlv_9= 'Options' otherlv_10= ':' ( (lv_options_11_0= RULE_STRING ) )* otherlv_12= ';' )
            	    // InternalMaude.g:172:19: otherlv_9= 'Options' otherlv_10= ':' ( (lv_options_11_0= RULE_STRING ) )* otherlv_12= ';'
            	    {
            	    otherlv_9=(Token)match(input,15,FOLLOW_3); 

            	    								newLeafNode(otherlv_9, grammarAccess.getModelAccess().getOptionsKeyword_2_0());
            	    							
            	    otherlv_10=(Token)match(input,12,FOLLOW_7); 

            	    								newLeafNode(otherlv_10, grammarAccess.getModelAccess().getColonKeyword_2_1());
            	    							
            	    // InternalMaude.g:180:8: ( (lv_options_11_0= RULE_STRING ) )*
            	    loop1:
            	    do {
            	        int alt1=2;
            	        int LA1_0 = input.LA(1);

            	        if ( (LA1_0==RULE_STRING) ) {
            	            alt1=1;
            	        }


            	        switch (alt1) {
            	    	case 1 :
            	    	    // InternalMaude.g:181:9: (lv_options_11_0= RULE_STRING )
            	    	    {
            	    	    // InternalMaude.g:181:9: (lv_options_11_0= RULE_STRING )
            	    	    // InternalMaude.g:182:10: lv_options_11_0= RULE_STRING
            	    	    {
            	    	    lv_options_11_0=(Token)match(input,RULE_STRING,FOLLOW_7); 

            	    	    										newLeafNode(lv_options_11_0, grammarAccess.getModelAccess().getOptionsSTRINGTerminalRuleCall_2_2_0());
            	    	    									

            	    	    										if (current==null) {
            	    	    											current = createModelElement(grammarAccess.getModelRule());
            	    	    										}
            	    	    										addWithLastConsumed(
            	    	    											current,
            	    	    											"options",
            	    	    											lv_options_11_0,
            	    	    											"org.eclipse.xtext.common.Terminals.STRING");
            	    	    									

            	    	    }


            	    	    }
            	    	    break;

            	    	default :
            	    	    break loop1;
            	        }
            	    } while (true);

            	    otherlv_12=(Token)match(input,13,FOLLOW_6); 

            	    								newLeafNode(otherlv_12, grammarAccess.getModelAccess().getSemicolonKeyword_2_3());
            	    							

            	    }


            	    }

            	     
            	    					getUnorderedGroupHelper().returnFromSelection(grammarAccess.getModelAccess().getUnorderedGroup());
            	    				

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt2 >= 1 ) break loop2;
                        EarlyExitException eee =
                            new EarlyExitException(2, input);
                        throw eee;
                }
                cnt2++;
            } while (true);

            if ( ! getUnorderedGroupHelper().canLeave(grammarAccess.getModelAccess().getUnorderedGroup()) ) {
                throw new FailedPredicateException(input, "ruleModel", "getUnorderedGroupHelper().canLeave(grammarAccess.getModelAccess().getUnorderedGroup())");
            }

            }


            }

             
            			  getUnorderedGroupHelper().leave(grammarAccess.getModelAccess().getUnorderedGroup());
            			

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleModel"

    // Delegated rules


 

    public static final BitSet FOLLOW_1 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_2 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_3 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_4 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_5 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_6 = new BitSet(new long[]{0x000000000000C802L});
    public static final BitSet FOLLOW_7 = new BitSet(new long[]{0x0000000000002010L});

}
