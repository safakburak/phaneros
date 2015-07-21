/*************************************************************************

"FreePastry" Peer-to-Peer Application Development Substrate

Copyright 2002, Rice University. All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:

- Redistributions of source code must retain the above copyright
notice, this list of conditions and the following disclaimer.

- Redistributions in binary form must reproduce the above copyright
notice, this list of conditions and the following disclaimer in the
documentation and/or other materials provided with the distribution.

- Neither  the name  of Rice  University (RICE) nor  the names  of its
contributors may be  used to endorse or promote  products derived from
this software without specific prior written permission.

This software is provided by RICE and the contributors on an "as is"
basis, without any representations or warranties of any kind, express
or implied including, but not limited to, representations or
warranties of non-infringement, merchantability or fitness for a
particular purpose. In no event shall RICE or contributors be liable
for any direct, indirect, incidental, special, exemplary, or
consequential damages (including, but not limited to, procurement of
substitute goods or services; loss of use, data, or profits; or
business interruption) however caused and on any theory of liability,
whether in contract, strict liability, or tort (including negligence
or otherwise) arising in any way out of the use of this software, even
if advised of the possibility of such damage.

********************************************************************************/

package planet.scribe.messaging;

import planet.commonapi.*;
import planet.scribe.*;

/**
 * The ack for a subscribe message.
 *
 * @version $Id: AbstractSubscribeMessage.java,v 1.1 2003/10/14 02:58:15 amislove Exp $
 *
 * @author Alan Mislove
 */
public abstract class AbstractSubscribeMessage extends ScribeMessage {

  /**
  * The id of this subscribe message
   */
  protected int id;

  /**
  * Constructor which takes a unique integer Id
   *
   * @param source Node source.
   * @param topic Message topic
   * @param id Subscribe message id.
   */
  public AbstractSubscribeMessage(NodeHandle source, Topic topic, int id) {
    super(source, topic);

    this.id = id;
  }

  /**
    * Returns this subscribe lost message's id
   *
   * @return The id of this subscribe lost message
   */
  public int getId() {
    return id;
  }
  

}

