package enigma;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;
import java.util.Random;

import enigma.Enigma.CharacterType;

/**
 * ロータ
 * @author gekka9
 *
 */
public class Rotor {
  /**
   * インデックスと文字の対応。リフレクターでないとき、暗号化・復号化ふごとにこれがrotateされていく。
   */
  private ArrayList<Character> charList;
  /**
   * 換字前後の対応
   */
  private HashMap<Character,Character> link;
  /**
   * リフレクターかどうかのフラグ
   */
  private boolean isReflector=false;
  /**
   * 文字種モード
   */
  private CharacterType mode;
  
  /**
   * リフレクターかどうか、扱う文字種を受け取り、インスタンスを生成する
   * @param isReflector リフレクターならtrue
   * @param mode 文字種
   */
  Rotor(boolean isReflector,Enigma.CharacterType mode){
    this.mode=mode;
    this.isReflector=isReflector;
    this.charList=new ArrayList<Character>();
    this.link=new HashMap<Character,Character>();
    this.resetCharList();
  }
  
  /**
   * 初期化する
   * @param seed シード値
   * @param offset
   */
  public void initialize(long seed, int offset){
    if(!this.isReflector){
      this.resetCharList();
    }
    ArrayList<Character> hashedList=(ArrayList<Character>) this.charList.clone();
    Collections.shuffle(hashedList, new Random(seed));
    Collections.rotate(this.charList, offset);
    int i=0;
    for(char aChar:this.charList){
      this.link.put(aChar, hashedList.get(i));
      i++;
    }
  }
  
  /**
   * コンソール側からの入力
   * @param index 入力
   * @return 出力
   */
  public int inAside(int index){
    char aChar=this.charList.get(index);
    aChar = this.link.get(aChar);
    int result = this.charList.indexOf(aChar);
    return result;
  }
  
  /**
   * リフレクター側からの入力
   * @param index 入力
   * @return 出力
   */
  public int inBside(int index){
    char aChar=this.charList.get(index);
    int result=0;
    if(this.link.containsValue(aChar)){
      Set<Entry<Character,Character>> keys = this.link.entrySet();
      Iterator<Entry<Character, Character>> iterator = keys.iterator();
      while(iterator.hasNext()){
        Entry<Character,Character> entry = iterator.next();
        Character key = entry.getKey();
        Character value = entry.getValue();
        if(aChar ==value){
          result=this.charList.indexOf(key);
          break;
        }
      }
    }
    return result;
  }
  /**
   * ローターの番号から文字を取得する
   * @param index インデックス
   * @return 文字
   */
  public char getChar(int index){
    return this.charList.get(index);
  }
  /**
   * ローターの文字から番号を取得する
   * @param target 文字
   * @return インデックス
   */
  public int getIndex(char target){
    return this.charList.indexOf(target);
  }
  
  /**
   * リストを再生成する。
   */
  public void resetCharList(){
    this.charList = new ArrayList<Character>();
    File file=null;
    switch(this.mode){
    case ALPHABET : 
      file = new File("./strings/alphabet.txt");
      break;
    case HIRAGANA :
      file = new File("./strings/hiragana.txt");
      break;
    case KATAKANA :
      file = new File("./strings/katakana.txt");
      break;
    case KANJI :
      file = new File("./strings/kanji.txt");
      break;
    case NUMBER :
      file = new File("./strings/number.txt");
      break;
    default:
      file = new File("./strings/alphabet.txt");
      break;
    }
    BufferedReader br;
    try {
      br = new BufferedReader(new FileReader(file));
    String str;
      str = br.readLine();
    while(str != null){
      char[] strChar=str.toCharArray();
      this.charList.add(strChar[0]);
      str = br.readLine();
    }
    br.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  /**
   * リストにその文字が入っているかどうか。入っていなければ非対応の文字
   * @param target 対象の文字
   * @return 入っていればtrue
   */
  public boolean isContains(char target){
    return this.charList.contains(target);
  }
  
  /**
   * ローターを回転する
   * @param offset 回転数
   */
  public void rotate(int offset){
    Collections.rotate(this.charList, offset);
  }
}
