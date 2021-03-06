package org.chrisle.netbeans.plugins.nbfilestructurenode;

import java.awt.Image;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Element;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.Task;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.MIMEResolver;
import org.openide.loaders.DataNode;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.FilterNode.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author ChrisLE
 */
@Messages({
    "LBL_InstallPluginAction_LOADER=Java file"
})
@MIMEResolver.ExtensionRegistration(
        displayName = "#LBL_InstallPluginAction_LOADER",
        mimeType = "text/x-java",
        extension = {"java", "JAVA"}
)
@DataObject.Registration(
        mimeType = "text/x-java",
        displayName = "#LBL_InstallPluginAction_LOADER",
        position = 300
)
@ActionReferences({
    @ActionReference(
            path = "Loaders/text/x-java/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.OpenAction"),
            position = 100,
            separatorAfter = 200
    ),
    @ActionReference(
            path = "Loaders/text/x-java/Actions",
            id = @ActionID(category = "Edit", id = "org.openide.actions.CutAction"),
            position = 300
    ),
    @ActionReference(
            path = "Loaders/text/x-java/Actions",
            id = @ActionID(category = "Edit", id = "org.openide.actions.CopyAction"),
            position = 400,
            separatorAfter = 500
    ),
    @ActionReference(
            path = "Loaders/text/x-java/Actions",
            id = @ActionID(category = "Edit", id = "org.openide.actions.DeleteAction"),
            position = 600
    ),
    @ActionReference(
            path = "Loaders/text/x-java/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.RenameAction"),
            position = 700,
            separatorAfter = 800
    ),
    @ActionReference(
            path = "Loaders/text/x-java/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.SaveAsTemplateAction"),
            position = 900,
            separatorAfter = 1000
    ),
    @ActionReference(
            path = "Loaders/text/x-java/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.FileSystemAction"),
            position = 1100,
            separatorAfter = 1200
    ),
    @ActionReference(
            path = "Loaders/text/x-java/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.ToolsAction"),
            position = 1300
    ),
    @ActionReference(
            path = "Loaders/text/x-java/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.PropertiesAction"),
            position = 1400
    )
})
public class ExtendedJavaDataObject extends MultiDataObject {
    public static List _list = new ArrayList();
    
    private static final String CLASS_IMAGE = "org/chrisle/netbeans/plugins/nbfilestructurenode/resources/class.png";
    private static final String METHOD_IMAGE = "org/chrisle/netbeans/plugins/nbfilestructurenode/resources/method.png";
    private static final String PUBLIC_IMAGE = "org/chrisle/netbeans/plugins/nbfilestructurenode/resources/public.png";

    public static Image getIconForElement(Element te) {
        Image result = null;

        if (null != te.getKind()) {
            switch (te.getKind()) {
                case CLASS:
                    if (te.getModifiers().contains(Modifier.ABSTRACT)) {
                        result = ImageUtilities.loadImage("org/chrisle/netbeans/plugins/nbfilestructurenode/resources/abstractClass.png");
                    } else if (te.getModifiers().contains(Modifier.FINAL)) {
                        result = ImageUtilities.mergeImages(ImageUtilities.loadImage(CLASS_IMAGE),
                                ImageUtilities.loadImage("org/chrisle/netbeans/plugins/nbfilestructurenode/resources/finalMark_dark.png"), 0, 0);
//                    else if(te.getClass().isInstance(Exception.class)) {
//                        result = ImageUtilities.loadImage("org/chrisle/netbeans/plugins/nbfilestructurenode/resources/exceptionClass.png");
                    } else if(te.getModifiers().contains(Modifier.PUBLIC)) {
                        result = ImageUtilities.mergeImages(ImageUtilities.loadImage(CLASS_IMAGE),
                                ImageUtilities.loadImage(PUBLIC_IMAGE), 15, 1);
                    } else {
                        result = ImageUtilities.loadImage(CLASS_IMAGE);
                    }
                    break;
                case INTERFACE:
                    result = ImageUtilities.loadImage("org/chrisle/netbeans/plugins/nbfilestructurenode/resources/interface.png");
                    break;
                case ENUM:
                    result = ImageUtilities.loadImage("org/chrisle/netbeans/plugins/nbfilestructurenode/resources/enum.png");
                    break;
                case ANNOTATION_TYPE:
                    result = ImageUtilities.loadImage("org/chrisle/netbeans/plugins/nbfilestructurenode/resources/annotationtype.png");
                    break;
                case METHOD:
                    if (te.getModifiers().contains(Modifier.PUBLIC)) {
                        result = ImageUtilities.mergeImages(ImageUtilities.loadImage(METHOD_IMAGE),
                                ImageUtilities.loadImage(PUBLIC_IMAGE), 15, 1);
                    } else {
                        result = ImageUtilities.loadImage(METHOD_IMAGE);
                    }
                    break;
                case FIELD:
                    result = ImageUtilities.loadImage("org/chrisle/netbeans/plugins/nbfilestructurenode/resources/field.png");
                    break;
                default:
                    result = ImageUtilities.loadImage(CLASS_IMAGE);
                    break;
            }
        }

        return result;
    }
    
    public ExtendedJavaDataObject(FileObject fo, MultiFileLoader loader) throws DataObjectExistsException {
        super(fo, loader);
        registerEditor("text/x-java", true);
    }

    @Override
    public Lookup getLookup() {
        return getCookieSet().getLookup();
    }
    
    private List<Element> _children;

    public List<Element> getChildren() {
        return _children;
    }

    public void setChildren(List<Element> _children) {
        this._children = _children;
    }

    @Override
    protected Node createNodeDelegate() {
        _children = getElementsFromFile(getPrimaryFile());
        Image iconForElement = null;

        if (!_children.isEmpty()) {
            Element firstElement = _children.iterator().next();
            iconForElement = getIconForElement(firstElement);
        }

        final Image icon = iconForElement;
        DataNode dataNode = new DataNode(this, Children.create(new JavaChildFactory(_children), true), getLookup()) {
            @Override
            public Image getIcon(int type) {
                if (null != icon) {
                    return icon;
                } else {
                    //show default ?
                    return ImageUtilities.loadImage(CLASS_IMAGE);
                }
            }

            @Override
            public Image getOpenedIcon(int type) {
                return getIcon(type);
            }
        };

        return dataNode;
    }

    private JavaSource _js;

    private List<Element> getElementsFromFile(FileObject fObj) throws IllegalArgumentException {
        final List<Element> result = new ArrayList<>();

        _js = JavaSource.forFileObject(fObj);

        if (_js == null) {
            return result;
        }

        try {
            _js.runUserActionTask(new Task<CompilationController>() {
                @Override
                public void run(CompilationController cc) throws Exception {
                    cc.toPhase(Phase.ELEMENTS_RESOLVED);

                    for (Element te : cc.getTopLevelElements()) {
                        result.add(te);
                    }
                }
            }, true);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        return result;
    }
}