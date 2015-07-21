package p2p.application.peer.components;

import p2p.network.IMessage;
import p2p.network.IEndPoint;
import p2p.network.IForwardListener;

/*
 * İki yöntem için de aynı anda eklenebilir ya da çıkarılabilir.
 * Bu sebeple karşılaştırma için gereklilik değil.
 * Belki çalışmaya ek olarak bahsedilebilir.
 * Geliştirme mevcut halinde bırakıldı.
 * Fikir, CHORD seviyesi peerlerden forward yapılırken 
 * şans eseri veriye sahip bir peerden geçilirse optimizasyon sağlamaktır.
 */

public class ForwardTracker implements IForwardListener 
{
	private IEndPoint mEndPoint;
	
	
	public ForwardTracker(IEndPoint endPoint) 
	{
		mEndPoint = endPoint;
		
		mEndPoint.setForwardDecider(this);
	}
	
	
	@Override
	public boolean forwardAllowed(String source, IMessage message) 
	{
		return true;
	}
}
