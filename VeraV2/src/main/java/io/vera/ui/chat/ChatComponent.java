package io.vera.ui.chat;

import io.vson.VsonValue;
import io.vson.elements.VsonArray;
import io.vson.elements.object.VsonObject;
import io.vson.enums.FileFormat;
import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class ChatComponent {
  private String text;
  
  private String translate;
  
  private String selector;
  
  private String insertion;
  
  private String scoreUsername;
  
  private String scoreObjective;
  
  private ChatColor color;
  
  private ClickEvent clickEvent;
  
  private HoverEvent hoverEvent;
  
  private ChatComponent(String text, String translate, String selector, String insertion, String scoreUsername, String scoreObjective, ChatColor color, ClickEvent clickEvent, HoverEvent hoverEvent, Boolean bold, Boolean italic, Boolean underlined, Boolean strikethrough, Boolean obfuscated) {
    this.text = text;
    this.translate = translate;
    this.selector = selector;
    this.insertion = insertion;
    this.scoreUsername = scoreUsername;
    this.scoreObjective = scoreObjective;
    this.color = color;
    this.clickEvent = clickEvent;
    this.hoverEvent = hoverEvent;
    this.bold = bold;
    this.italic = italic;
    this.underlined = underlined;
    this.strikethrough = strikethrough;
    this.obfuscated = obfuscated;
  }
  
  public String getText() {
    return this.text;
  }
  
  public ChatComponent setText(String text) {
    this.text = text;
    return this;
  }
  
  public String getTranslate() {
    return this.translate;
  }
  
  public ChatComponent setTranslate(String translate) {
    this.translate = translate;
    return this;
  }
  
  public String getSelector() {
    return this.selector;
  }
  
  public ChatComponent setSelector(String selector) {
    this.selector = selector;
    return this;
  }
  
  public String getInsertion() {
    return this.insertion;
  }
  
  public ChatComponent setInsertion(String insertion) {
    this.insertion = insertion;
    return this;
  }
  
  public String getScoreUsername() {
    return this.scoreUsername;
  }
  
  public ChatComponent setScoreUsername(String scoreUsername) {
    this.scoreUsername = scoreUsername;
    return this;
  }
  
  public String getScoreObjective() {
    return this.scoreObjective;
  }
  
  public ChatComponent setScoreObjective(String scoreObjective) {
    this.scoreObjective = scoreObjective;
    return this;
  }
  
  public ChatColor getColor() {
    return this.color;
  }
  
  public ChatComponent setColor(ChatColor color) {
    this.color = color;
    return this;
  }
  
  public ClickEvent getClickEvent() {
    return this.clickEvent;
  }
  
  public ChatComponent setClickEvent(ClickEvent clickEvent) {
    this.clickEvent = clickEvent;
    return this;
  }
  
  public HoverEvent getHoverEvent() {
    return this.hoverEvent;
  }
  
  public ChatComponent setHoverEvent(HoverEvent hoverEvent) {
    this.hoverEvent = hoverEvent;
    return this;
  }
  
  private final List<ChatComponent> with = new ArrayList<>();
  
  private final List<ChatComponent> extra = new ArrayList<>();
  
  private Boolean bold;
  
  private Boolean italic;
  
  private Boolean underlined;
  
  private Boolean strikethrough;
  
  private Boolean obfuscated;
  
  public List<ChatComponent> getWith() {
    return this.with;
  }
  
  public ChatComponent addWith(ChatComponent component) {
    if (!hasWith(component, true))
      this.with.add(component); 
    return this;
  }
  
  public ChatComponent addWith(String string) {
    return addWith(new StringChatComponent(string));
  }
  
  public boolean hasWith(ChatComponent component, boolean recursive) {
    List<ChatComponent> with = this.with;
    if (component == this || with.contains(component))
      return true; 
    if (!recursive)
      return false; 
    for (ChatComponent child : with) {
      if (child.hasWith(component, true))
        return true; 
    } 
    return false;
  }
  
  public List<ChatComponent> getExtra() {
    return this.extra;
  }
  
  public ChatComponent addExtra(ChatComponent component) {
    if (!hasExtra(component, true))
      this.extra.add(component); 
    return this;
  }
  
  public ChatComponent addExtra(String string) {
    this.extra.add(new StringChatComponent(string));
    return this;
  }
  
  public boolean hasExtra(ChatComponent component, boolean recursive) {
    List<ChatComponent> extra = this.extra;
    if (component == this || extra.contains(component))
      return true; 
    if (!recursive)
      return false; 
    for (ChatComponent child : extra) {
      if (child.hasExtra(component, true))
        return true; 
    } 
    return false;
  }
  
  public boolean isBold() {
    Boolean flag = this.bold;
    return (flag != null && flag.booleanValue());
  }
  
  public ChatComponent setBold(boolean bold) {
    this.bold = Boolean.valueOf(bold);
    return this;
  }
  
  public boolean isItalic() {
    Boolean flag = this.italic;
    return (flag != null && flag.booleanValue());
  }
  
  public ChatComponent setItalic(boolean italic) {
    this.italic = Boolean.valueOf(italic);
    return this;
  }
  
  public boolean isUnderlined() {
    Boolean flag = this.underlined;
    return (flag != null && flag.booleanValue());
  }
  
  public ChatComponent setUnderlined(boolean underlined) {
    this.underlined = Boolean.valueOf(underlined);
    return this;
  }
  
  public boolean isStrikethrough() {
    Boolean flag = this.strikethrough;
    return (flag != null && flag.booleanValue());
  }
  
  public ChatComponent setStrikethrough(boolean strikethrough) {
    this.strikethrough = Boolean.valueOf(strikethrough);
    return this;
  }
  
  public boolean isObfuscated() {
    Boolean flag = this.obfuscated;
    return (flag != null && flag.booleanValue());
  }
  
  public ChatComponent setObfuscated(boolean obfuscated) {
    this.obfuscated = Boolean.valueOf(obfuscated);
    return this;
  }
  
  public VsonValue stripColor() {
    VsonObject json = new VsonObject();
    String text = this.text;
    if (text != null)
      json.append("text", text); 
    String translate = this.translate;
    if (translate != null) {
      json.append("translate", translate);
      VsonArray array = new VsonArray();
      this.with.forEach(e -> array.append(e.stripColor()));
      json.append("with", array);
    } 
    String scoreUsername = this.scoreUsername;
    String scoreObjective = this.scoreObjective;
    if (scoreUsername != null && scoreObjective != null) {
      VsonObject score = new VsonObject();
      score.append("name", scoreUsername);
      score.append("objective", scoreObjective);
      json.append("score", score);
    } 
    String selector = this.selector;
    if (selector != null)
      json.append("selector", selector); 
    List<ChatComponent> extra = this.extra;
    if (!extra.isEmpty()) {
      VsonArray extraArray = new VsonArray();
      extra.forEach(e -> extraArray.append(e.stripColor()));
      json.append("extra", extraArray);
    } 
    ClickEvent clickEvent = this.clickEvent;
    if (clickEvent != null)
      json.append("clickEvent", clickEvent.asJson()); 
    HoverEvent hoverEvent = this.hoverEvent;
    if (hoverEvent != null)
      json.append("hoverEvent", hoverEvent.asJson()); 
    String insertion = this.insertion;
    if (insertion != null)
      json.append("insertion", insertion); 
    return (VsonValue)json;
  }
  
  public VsonValue asJson() {
    VsonObject json = new VsonObject();
    String text = this.text;
    if (text != null)
      json.append("text", text); 
    String translate = this.translate;
    if (translate != null) {
      json.append("translate", translate);
      VsonArray array = new VsonArray();
      this.with.forEach(e -> array.append(e.asJson()));
      json.append("with", array);
    } 
    String scoreUsername = this.scoreUsername;
    String scoreObjective = this.scoreObjective;
    if (scoreUsername != null && scoreObjective != null) {
      VsonObject score = new VsonObject();
      score.append("name", scoreUsername);
      score.append("objective", scoreObjective);
      json.append("score", score);
    } 
    String selector = this.selector;
    if (selector != null)
      json.append("selector", selector); 
    List<ChatComponent> extra = this.extra;
    if (!extra.isEmpty()) {
      VsonArray extraArray = new VsonArray();
      extra.forEach(e -> extraArray.append(e.asJson()));
      json.append("extra", extraArray);
    } 
    Boolean isBold = this.bold;
    if (isBold != null)
      json.append("bold", isBold); 
    Boolean isItalic = this.italic;
    if (isItalic != null)
      json.append("italic", isItalic); 
    Boolean isUnderlined = this.underlined;
    if (isUnderlined != null)
      json.append("underlined", isUnderlined); 
    Boolean isStrikethrough = this.strikethrough;
    if (isStrikethrough != null)
      json.append("strikethrough", isStrikethrough); 
    Boolean isObfuscated = this.obfuscated;
    if (isObfuscated != null)
      json.append("obfuscated", isObfuscated); 
    ChatColor color = this.color;
    if (color != null && !color.isFormat())
      json.append("color", color.name().toLowerCase()); 
    ClickEvent clickEvent = this.clickEvent;
    if (clickEvent != null)
      json.append("clickEvent", clickEvent.asJson()); 
    HoverEvent hoverEvent = this.hoverEvent;
    if (hoverEvent != null)
      json.append("hoverEvent", hoverEvent.asJson()); 
    String insertion = this.insertion;
    if (insertion != null)
      json.append("insertion", insertion); 
    return (VsonValue)json;
  }
  
  public String toString() {
    return asJson().toString(FileFormat.RAW_JSON);
  }
  
  public static ChatComponent create() {
    return new ChatComponent();
  }
  
  public static ChatComponent empty() {
    return create().setText("");
  }
  
  public static ChatComponent text(String text) {
    return create().setText(text);
  }
  
  public static ChatComponent fromJson(VsonObject json) {
    ChatComponent cc = create();
    VsonValue text = json.get("text");
    if (text != null)
      cc.setText(text.asString()); 
    VsonValue translate = json.get("translate");
    if (translate != null)
      cc.setTranslate(translate.asString()); 
    VsonValue with = json.get("with");
    if (with != null) {
      VsonArray array = with.asArray();
      for (int i = 0, j = array.size(); i < j; i++) {
        VsonValue el = array.get(i);
        if (el.isString() || el.isNumber() || el.isBoolean()) {
          cc.addWith(el.asString());
        } else if (el.isObject()) {
          cc.addWith(fromJson(el.asVsonObject()));
        } 
      } 
    } 
    VsonValue score = json.get("score");
    if (score != null) {
      VsonObject scoreArray = score.asVsonObject();
      cc.setScoreUsername(scoreArray.get("name").asString());
      cc.setScoreObjective(scoreArray.get("objective").asString());
    } 
    VsonValue selector = json.get("selector");
    if (selector != null)
      cc.setSelector(selector.asString()); 
    VsonValue extra = json.get("extra");
    if (extra != null) {
      VsonArray array = extra.asArray();
      for (int i = 0, j = array.size(); i < j; i++) {
        VsonValue el = array.get(i);
        if (el.isString() || el.isNumber() || el.isBoolean()) {
          cc.addExtra(el.asString());
        } else if (el.isObject()) {
          cc.addExtra(fromJson(el.asVsonObject()));
        } 
      } 
    } 
    VsonValue bold = json.get("bold");
    if (bold != null)
      cc.setBold(bold.asBoolean().booleanValue()); 
    VsonValue italic = json.get("italic");
    if (italic != null)
      cc.setItalic(italic.asBoolean().booleanValue()); 
    VsonValue underlined = json.get("underlined");
    if (underlined != null)
      cc.setUnderlined(underlined.asBoolean().booleanValue()); 
    VsonValue strikethrough = json.get("strikethrough");
    if (strikethrough != null)
      cc.setStrikethrough(strikethrough.asBoolean().booleanValue()); 
    VsonValue obfuscated = json.get("obfuscated");
    if (obfuscated != null)
      cc.setObfuscated(obfuscated.asBoolean().booleanValue()); 
    VsonValue color = json.get("color");
    if (color != null)
      cc.setColor(ChatColor.valueOf(color.asString().toUpperCase())); 
    VsonValue clickEvent = json.get("clickEvent");
    if (clickEvent != null)
      cc.setClickEvent(ClickEvent.fromJson(clickEvent.asVsonObject())); 
    VsonValue hoverEvent = json.get("hoverEvent");
    if (hoverEvent != null)
      cc.setHoverEvent(HoverEvent.fromJson((VsonValue)hoverEvent.asVsonObject())); 
    VsonValue insertion = json.get("insertion");
    if (insertion != null)
      cc.setInsertion(insertion.asString()); 
    return cc;
  }
  
  public static ChatComponent fromFormat(String format) {
    char[] chars = format.toCharArray();
    String currentText = "";
    ChatColor currentColor = null;
    ChatComponent component = create(), currentComponent = null;
    boolean obfuscate = false, strikethrough = obfuscate, underline = strikethrough, italic = underline, bold = italic;
    for (int i = 0, j = chars.length; i < j; i++) {
      boolean prevSection = (i != 0 && chars[i - 1] == '§');
      char c = chars[i];
      if (prevSection) {
        ChatColor color = ChatColor.of(c);
        if (color != null) {
          ChatComponent curr = (currentComponent == null) ? component : currentComponent;
          if (!currentText.isEmpty()) {
            curr.setText(currentText).setColor(currentColor);
            if (currentComponent != null)
              component.addExtra(currentComponent); 
            curr = currentComponent = create();
            currentText = "";
          } 
          if (color.isColor()) {
            currentColor = color;
            if (bold)
              curr.setBold(bold = false); 
            if (italic)
              curr.setItalic(italic = false); 
            if (underline)
              curr.setUnderlined(underline = false); 
            if (strikethrough)
              curr.setStrikethrough(strikethrough = false); 
            if (obfuscate)
              curr.setObfuscated(obfuscate = false); 
          } else {
            switch (color) {
              case BOLD:
                if (!bold)
                  curr.setBold(bold = true); 
                break;
              case ITALIC:
                if (!italic)
                  curr.setItalic(italic = true); 
                break;
              case UNDERLINE:
                if (!underline)
                  curr.setUnderlined(underline = true); 
                break;
              case STRIKETHROUGH:
                if (!strikethrough)
                  curr.setStrikethrough(strikethrough = true); 
                break;
              case OBFUSCATED:
                if (!obfuscate)
                  curr.setObfuscated(obfuscate = true); 
                break;
              case RESET:
                currentColor = null;
                if (bold)
                  curr.setBold(bold = false); 
                if (italic)
                  curr.setItalic(italic = false); 
                if (underline)
                  curr.setUnderlined(underline = false); 
                if (strikethrough)
                  curr.setStrikethrough(strikethrough = false); 
                if (obfuscate)
                  curr.setObfuscated(obfuscate = false); 
                break;
            } 
          } 
        } 
      } else if (c != '§') {
        currentText = currentText + c;
      } 
    } 
    if (!currentText.isEmpty())
      component.addExtra(currentComponent.setText(currentText).setColor(currentColor)); 
    return component;
  }
  
  public boolean equals(Object o) {
    if (o == null || o == this || o.getClass() != getClass())
      return (o == this); 
    ChatComponent cc = (ChatComponent)o;
    return asJson().equals(cc.asJson());
  }
  
  public int hashCode() {
    return asJson().hashCode();
  }
  
  public ChatComponent() {}
  
  private static final class StringChatComponent extends ChatComponent {
    private final String string;
    
    @ConstructorProperties({"string"})
    public StringChatComponent(String string) {
      this.string = string;
    }
    
    public String getString() {
      return this.string;
    }
    
    public VsonValue stripColor() {
      return asJson();
    }
    
    public VsonValue asJson() {
      return VsonValue.valueOf(this.string);
    }
  }
}
