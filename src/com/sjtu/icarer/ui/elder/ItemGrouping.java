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
	
	private final List<ElderItem> elderItems;
	private List<ElderItemSection> itemSections = new ArrayList<ElderItemSection>();
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
    
    public List<ElderItemSection> getItemSectionList(){
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
    	int headerPosition = itemSections.size();
    	itemSections.add(new ElderItemSection(header,true,1,headerPosition));
    	for(ElderItem item : items){
    		itemSections.add(new ElderItemSection(item.getCareItemName(),false,1,headerPosition));
    	}
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
