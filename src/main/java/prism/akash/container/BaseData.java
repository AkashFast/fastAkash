package prism.akash.container;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@SuppressWarnings("rawtypes")
public class BaseData extends HashMap implements Map{

    private static final long serialVersionUID = 1L;

    Map map = null;
    HttpServletRequest request;
    @SuppressWarnings("unchecked")
    public BaseData(HttpServletRequest request){
        this.request = request;
        Map properties = request.getParameterMap();
        Map returnMap = new HashMap();
        Iterator entries = properties.entrySet().iterator();
        Entry entry;
        String name = "";
        String value = "";
        while (entries.hasNext()) {
            entry = (Entry) entries.next();
            name = (String) entry.getKey();
            Object valueObj = entry.getValue();
            if(null == valueObj){
                value = "";
            }else if(valueObj instanceof String[]){
                String[] values = (String[])valueObj;
                for(int i=0;i<values.length;i++){
                    value = values[i] + ",";
                }
                value = value.substring(0, value.length()-1);
            }else{
                value = valueObj.toString();
            }
            returnMap.put(name, value);
        }
        map = returnMap;
    }

    public BaseData() {
        map = new HashMap();
    }

    @Override
    public Object get(Object key) {
        Object obj = null;
        if(map.get(key) instanceof Object[]) {
            Object[] arr = (Object[])map.get(key);
            obj = request == null ? arr:(request.getParameter((String)key) == null ? arr:arr[0]);
        } else {
            obj = map.get(key);
        }
        return obj;
    }

    public Integer getInter(Object key){
        return get(key) == null ? -1 : Integer.parseInt(get(key)+"");
    }

    public Double  getDouble(Object key) {
        return get(key) == null ? 0.0 : Double.parseDouble(getString(key).isEmpty() ? "0.0" : getString(key));
    }

    public String getString(Object key) {
        return get(key) == null ? "" : (String) get(key);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object put(Object key, Object value) {
        return map.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        return map.remove(key);
    }

    public void clear() {
        map.clear();
    }

    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    public Set entrySet() {
        return map.entrySet();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public Set keySet() {
        return map.keySet();
    }

    @SuppressWarnings("unchecked")
    public void putAll(Map t) {
        map.putAll(t);
    }

    public int size() {
        return map.size();
    }

    public Collection values() {
        return map.values();
    }

    public String parsePage(){
        StringBuffer sb = new StringBuffer();
        Iterator<String> iteratorSet = this.keySet().iterator();
        while (iteratorSet.hasNext()){
            String key = iteratorSet.next();
            sb.append(key).append("=")
                    .append(this.get(key)).append("&");
        }
        if (sb.length() > 0) sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }
}

