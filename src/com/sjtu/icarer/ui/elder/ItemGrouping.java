package com.sjtu.icarer.ui.elder;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.sjtu.icarer.common.utils.TimeUtils;
import com.sjtu.icarer.model.ElderItem;

import static com.sjtu.icarer.common.utils.TimeUtils.TIME_FORMAT;
public class ItemGrouping {
	/*special requirements:
	 * if time is not filled in, the server returns 00:00:00 in default
	 * normal items will not be done at midnight
	 * so we just regard 00:00:00 as null
	 * @see  JsonTimeDeserializer
	 * @author KangShiyong
	 * */
	private final List<ElderItem> elderItems;
	private List<HeaderOrItemSection> itemSections = new ArrayList<HeaderOrItemSection>();
	private final String currentTime;
	
	private List<ElderItem> overdueItems= new ArrayList<ElderItem>();//sort by end time
	private List<ElderItem> currentItems= new ArrayList<ElderItem>();//sort by start time
	private List<ElderItem> futureItems = new ArrayList<ElderItem>();//sort by start time
	private List<ElderItem> unknownItems= new ArrayList<ElderItem>();
	
    public ItemGrouping(List<ElderItem> elderItems){
    	this.elderItems = elderItems;
    	this.currentTime = TimeUtils.getCurrentTimeInString(TIME_FORMAT);
    	dispatch();
    	setComparableByEndTime(overdueItems);
    	setComparableByEndTime(currentItems);
    	setComparableByStartTime(futureItems);
    	collect();
    }
    
    public List<HeaderOrItemSection> getItemSectionList(){
    	return itemSections;
    }
    
    private void dispatch(){
    	for(ElderItem item:elderItems){
    		Time start = item.getStartTime();
    		Time end = item.getEndTime();
    		if(start!=null && (currentTime.compareTo(start.toString()))<=0){
    			futureItems.add(item);
    		}else if(end!=null &&(currentTime.compareTo(end.toString())>=0)){
    			overdueItems.add(item);
    		}else if(start!=null && end!=null 
    				&& currentTime.compareTo(start.toString())>=0
    				&& currentTime.compareTo(end.toString())<=0){
    			currentItems.add(item);
    		}else{
    			unknownItems.add(item);
    		}
    	}
    }
    
    private void collect(){
    	addToSection(overdueItems,"overdue");
    	addToSection(currentItems, "current");
    	addToSection(futureItems, "future");
    	addToSection(unknownItems,"unknown");
    }
    
    private void addToSection(List<ElderItem> items,String header){
    	if(items==null||items.size()==0)return;
    	int headerPosition = itemSections.size();
    	itemSections.add(new HeaderOrItemSection(header,1,headerPosition));
    	for(ElderItem item : items){
    		itemSections.add(new HeaderOrItemSection(item,1,headerPosition));
    	}
    }
    
    private void translateZeroToNull(){
    	
    }
    /*
     * set comparator for the given list
     * */
    private void setComparableByStartTime(List<ElderItem> items){
    	Collections.sort(items, new Comparator<ElderItem>(){
			@Override
			public int compare(ElderItem lhs, ElderItem rhs) {
				Time ls = lhs.getStartTime();
				Time rs = rhs.getStartTime();
				//check null anyway, after dispatching though, items must have start time
				if(ls==null)return 1;
				if(rs==null)return -1;
				return ls.compareTo(rs);
			}
    		
    	});
    }
    
    /*
     * set comparator for the given list
     * */
    private void setComparableByEndTime(List<ElderItem> items){
    	Collections.sort(items, new Comparator<ElderItem>(){
			@Override
			public int compare(ElderItem lhs, ElderItem rhs) {
				Time le = lhs.getEndTime();
				Time re = rhs.getEndTime();
				//check null anyway, after dispatching though, items must have end time
				if(le==null)return 1;
				if(re==null)return -1;
				return le.compareTo(re);
			}
    		
    	});
    }
    
}
