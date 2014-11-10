package org.jclouds.compute.declarativestub.core;

//Not sure what problem shall solve the following code:
// @Fresh({@FreshObjects(cls=Entry.class, num=1),
// @FreshObjects(cls=Entry[].class, num=1)})
// @Override
// public void setEmailAddress(String name, String email) {
// Squander.exe(this, new Class<?>[]{String.class, String.class}, new
// Object[]{name, email});
// }
import java.util.Set;

import org.jclouds.compute.domain.Image;

import edu.mit.csail.sdg.annotations.Ensures;
import edu.mit.csail.sdg.annotations.FreshObjects;
import edu.mit.csail.sdg.annotations.Invariant;
import edu.mit.csail.sdg.annotations.Modifies;
import edu.mit.csail.sdg.annotations.Requires;
import edu.mit.csail.sdg.annotations.SpecField;
import edu.mit.csail.sdg.squander.Squander;

@SpecField({ "vms : set DeclarativeNode", "images : set org.jclouds.compute.domain.Image" })
@Invariant({ "all vmA : this.vms | all vmB : this.vms - vmA | vmA.id != vmB.id",
		"all imageA : this.images | all imageB : this.images - imageA | imageA.id != imageB.id" })
public class DeclarativeCloud {

	public DeclarativeCloud(Set<Image> images) {
		init(images);
	}

	@Requires({ 
//			"some _images.elts",
			// This is to require that the invariants are ok also before !
//			"all i : _images.elts | i.id != null && i.status != null && i.location != null",
//			"all i : _images.elts | all j : _images.elts - i | i.id !=  j.id", 
	})
	// Same Elements, same relations !
	@Ensures({ "no this.vms",//
			"this.images = _images.elts",// Note that this work as long as we do not introduce invariants !
	})
	@Modifies({ "this.vms",// /
			"this.images" })
	private void init(Set<Image> _images) {
		Squander.exe(this, _images);
	}

	@FreshObjects(num = 1, cls = Set.class, typeParams = { Image.class })
	@Ensures({ "return.elts == this.images" })
	@Modifies({ "return.elts" })
	public Set<Image> getImages() {
		return Squander.exe(this);
	}
	// java.lang.AssertionError: can't add spec field after calling finish(): DECLARATION:
	// "elts   : set (org.jclouds.compute.domain.Image)"

	// If use the invariant: @Invariant("this.id != null") inside Image.jfspec it will not find any solution ! Why so ?
	// If use the invariant: @Invariant("this.status != null") inside Image.jfspec it will not find any solution ! Why
	// so ?
	// If use the invariant: @Invariant("this.id != null") inside Location.jfspec it will not find any solution for the
	// initialization ! Why so
	// ?
	// If use the invariant: @Invariant("this.id != null") inside Image.jfspec it will not find any solution ! Why so ?

	// I think that the difficulty is understanding the JavaScene, it seems that we need to mention all the elements
	// that we are using inside the spec somewhere !!

}
