import java.util.ArrayList;
import java.util.HashSet;

import com.cycling74.max.Atom;
import com.cycling74.max.MaxBox;
import com.cycling74.max.MaxObject;
import com.cycling74.max.MaxPatcher;

public class BasketManager1 extends MaxObject{
	HashSet <Sample> currentlyInBasket;
	ArrayList<BasketItem> basketItems;
	MaxPatcher parentPatcher;
	MaxBox viewManagerSend;
	MaxBox basketGate;
	
	
	public BasketManager1(){
		currentlyInBasket =  new HashSet<Sample>();
		basketItems =  new ArrayList<BasketItem>();
		parentPatcher = this.getParentPatcher();
		viewManagerSend = parentPatcher.getNamedBox("viewManagerSend");
		basketGate = parentPatcher.getNamedBox("basketGate");
		
	}
	
	public void appendSamplesToBasket(String [] filePathArray){
		post("appendSampleToBasket() method was called");
		ArrayList <String>  samplesToUpdate = new ArrayList<String>();
	
		for(int i = 0; i< filePathArray.length; i++){
			post(filePathArray[i]);
			
			Sample testSample = new Sample(filePathArray[i]);
			if(!currentlyInBasket.contains(testSample)){
				BasketItem newBasketItem = new BasketItem(filePathArray[i], parentPatcher, basketItems.size());
				basketItems.add(newBasketItem);
				currentlyInBasket.add(testSample);
				samplesToUpdate.add( filePathArray[i]);
				
				
			}else{
				post("Is already in Basket: "+ testSample.getFilePath());
			}
		}
		
		//update the views
		if(samplesToUpdate.size()>0){
		sendAppendInfoToViews(samplesToUpdate);
		}
		
	
		
	}
	
	private void sendAppendInfoToViews(ArrayList <String> filePathList){
		post("sendAppendInfoToViews() method was called");
//		for(String filePath : filePathList){
//			post(filePath);
//		}
		
		Atom [] atomSendArray  = new Atom [filePathList.size()*2+1];
		atomSendArray[0]= Atom.newAtom("setSelectedSamples");
		
		int loopCounter = 1;
		for(int i = 0; i< filePathList.size(); i++){
			atomSendArray[loopCounter]= Atom.newAtom(filePathList.get(i));
			atomSendArray[loopCounter+1]= Atom.newAtom("quad");
			loopCounter = loopCounter +2;
			
		}
		
	
		
		viewManagerSend.send("selectSamplesInAllViews", atomSendArray);
	}
	
	public void sendButtonInfoToAllViews(String [] buttonInfo){
		post("sendButtonInfoToAllViews() method was called");
		post("filePath: "+buttonInfo[0]);
		post("shape: "+ buttonInfo[1]);
		Atom [] sendArray  = new Atom []{Atom.newAtom("setSelectedSamples"),Atom.newAtom(buttonInfo[0]), Atom.newAtom(buttonInfo[1])};
		viewManagerSend.send("selectSamplesInAllViews", sendArray);
	}
	

	
	
	public void removeBasketItem(String filePath){
		
		
		
			post("removeBasketItem() method: "+filePath);
			
			Atom [] sendArray  = new Atom []{Atom.newAtom("unSelectSamples"),Atom.newAtom(filePath), Atom.newAtom("circle")};
			viewManagerSend.send("unSelectSamplesInAllViews", sendArray);
		
			int removeIndex = 0;
			for(BasketItem basketItem : basketItems){
				if(basketItem.getFilePath().equals(filePath)){
					basketItem.getSymButton().getJsui().remove();
					basketItem.getSymButton().getPanel().remove();
					basketItem.getPlaylistObject().remove();
					basketItem.getDict().remove();
					basketItem.getDictListenerButton().remove();
					basketItem.getDictListener().remove();
				
					removeIndex = basketItems.indexOf(basketItem);
					basketItems.remove(removeIndex);
					currentlyInBasket.remove(new Sample(filePath));
					break;
				}
			}
		
		post("size basketItmes after single remove: "+basketItems.size());
		post("size currentlyInBasket after single remove: "+currentlyInBasket.size());
		
	}
	
	
	public void removeAllSamples(){
		post("removeAllSampels() method was called");
		post("size of basketItems: "+basketItems.size());
		
	
		

		
		
		Atom [] sendArray =  new Atom [basketItems.size()*2 +1];
		sendArray[0]= Atom.newAtom("unSelectSamples");
		int loopCounter = 1;
		for(BasketItem basketItem : basketItems){
			
			sendArray[loopCounter]= Atom.newAtom(basketItem.getFilePath());
			sendArray[loopCounter+1]= Atom.newAtom("circle");
			loopCounter  = loopCounter+2;
		}
		viewManagerSend.send("unSelectSamplesInAllViews", sendArray);
		
//		while(basketItems.size() > 0){
//		for(int i = 0; i< basketItems.size();i++){
//			post("Sending remove for: "+basketItems.get(i).getFilePath());
//			basketItems.get(i).getPlaylistObject().send("remove", new Atom []{Atom.newAtom(1)});
//			//removeBasketItem(basketItems.get(i).getFilePath());
//		}	
//		}
		
		
		
		for(BasketItem basketItem : basketItems){
				post("Trying to remove: "+basketItem.getFilePath());
				basketItem.getPlaylistObject().send("remove", new Atom []{Atom.newAtom(1)});
				basketItem.getSymButton().getJsui().remove();
				basketItem.getSymButton().getPanel().remove();
				basketItem.getPlaylistObject().remove();
				basketItem.getDict().remove();
				basketItem.getDictListenerButton().remove();
				basketItem.getDictListener().remove();
				
			
			
		}
		
		
		currentlyInBasket =  new HashSet<Sample>();
		basketItems =  new ArrayList<BasketItem>();
		//open the gate again
		basketGate.send(1);
	}
	
	
}
